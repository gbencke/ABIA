   /*******************************************************/
   /*      "C" Language Integrated Production System      */
   /*                                                     */
   /*               CLIPS Version 6.20  01/31/02          */
   /*                                                     */
   /*                                                     */
   /*******************************************************/

/*************************************************************/
/* Purpose: Query Functions for Objects                      */
/*                                                           */
/* Principal Programmer(s):                                  */
/*      Brian L. Donnell                                     */
/*                                                           */
/* Contributing Programmer(s):                               */
/*                                                           */
/*                                                           */
/* Revision History:                                         */
/*                                                           */
/*************************************************************/

/* =========================================
   *****************************************
               EXTERNAL DEFINITIONS
   =========================================
   ***************************************** */
#include "setup.h"

#if INSTANCE_SET_QUERIES

#include "argacces.h"
#include "classcom.h"
#include "classfun.h"
#include "envrnmnt.h"
#include "memalloc.h"
#include "exprnpsr.h"
#include "insfun.h"
#include "insmngr.h"
#include "insqypsr.h"
#include "prcdrfun.h"
#include "router.h"
#include "utility.h"

#define _INSQUERY_SOURCE_
#include "insquery.h"

/* =========================================
   *****************************************
      INTERNALLY VISIBLE FUNCTION HEADERS
   =========================================
   ***************************************** */

static void PushQueryCore(void *);
static void PopQueryCore(void *);
static QUERY_CORE *FindQueryCore(void *,int);
static QUERY_CLASS *DetermineQueryClasses(void *,EXPRESSION *,char *,unsigned *);
static QUERY_CLASS *FormChain(void *,char *,DATA_OBJECT *);
static void DeleteQueryClasses(void *,QUERY_CLASS *);
static int TestForFirstInChain(void *,QUERY_CLASS *,int);
static int TestForFirstInstanceInClass(void *,struct defmodule *,int,DEFCLASS *,QUERY_CLASS *,int);
static void TestEntireChain(void *,QUERY_CLASS *,int);
static void TestEntireClass(void *,struct defmodule *,int,DEFCLASS *,QUERY_CLASS *,int);
static void AddSolution(void *);
static void PopQuerySoln(void *);

/****************************************************
  NAME         : SetupQuery
  DESCRIPTION  : Initializes instance query H/L
                   functions and parsers
  INPUTS       : None
  RETURNS      : Nothing useful
  SIDE EFFECTS : Sets up kernel functions and parsers
  NOTES        : None
 ****************************************************/
globle void SetupQuery(
  void *theEnv)
  {
   AllocateEnvironmentData(theEnv,INSTANCE_QUERY_DATA,sizeof(struct instanceQueryData),NULL);

#if ! RUN_TIME
   InstanceQueryData(theEnv)->QUERY_DELIMETER_SYMBOL = (SYMBOL_HN *) EnvAddSymbol(theEnv,QUERY_DELIMETER_STRING);
   IncrementSymbolCount(InstanceQueryData(theEnv)->QUERY_DELIMETER_SYMBOL);

   EnvDefineFunction2(theEnv,"(query-instance)",'o',
                  PTIEF GetQueryInstance,"GetQueryInstance",NULL);

   EnvDefineFunction2(theEnv,"(query-instance-slot)",'u',
                  PTIEF GetQueryInstanceSlot,"GetQueryInstanceSlot",NULL);

   EnvDefineFunction2(theEnv,"any-instancep",'b',PTIEF AnyInstances,"AnyInstances",NULL);
   AddFunctionParser(theEnv,"any-instancep",ParseQueryNoAction);

   EnvDefineFunction2(theEnv,"find-instance",'m',
                  PTIEF QueryFindInstance,"QueryFindInstance",NULL);
   AddFunctionParser(theEnv,"find-instance",ParseQueryNoAction);

   EnvDefineFunction2(theEnv,"find-all-instances",'m',
                  PTIEF QueryFindAllInstances,"QueryFindAllInstances",NULL);
   AddFunctionParser(theEnv,"find-all-instances",ParseQueryNoAction);

   EnvDefineFunction2(theEnv,"do-for-instance",'u',
                  PTIEF QueryDoForInstance,"QueryDoForInstance",NULL);
   AddFunctionParser(theEnv,"do-for-instance",ParseQueryAction);

   EnvDefineFunction2(theEnv,"do-for-all-instances",'u',
                  PTIEF QueryDoForAllInstances,"QueryDoForAllInstances",NULL);
   AddFunctionParser(theEnv,"do-for-all-instances",ParseQueryAction);

   EnvDefineFunction2(theEnv,"delayed-do-for-all-instances",'u',
                  PTIEF DelayedQueryDoForAllInstances,
                  "DelayedQueryDoForAllInstances",NULL);
   AddFunctionParser(theEnv,"delayed-do-for-all-instances",ParseQueryAction);
#endif
  }

/*************************************************************
  NAME         : GetQueryInstance
  DESCRIPTION  : Internal function for referring to instance
                    array on instance-queries
  INPUTS       : None
  RETURNS      : The name of the specified instance-set member
  SIDE EFFECTS : None
  NOTES        : H/L Syntax : ((query-instance) <index>)
 *************************************************************/
globle SYMBOL_HN *GetQueryInstance(
  void *theEnv)
  {
   register QUERY_CORE *core;

   core = FindQueryCore(theEnv,DOPToInteger(GetFirstArgument()));
   return(GetFullInstanceName(theEnv,core->solns[DOPToInteger(GetFirstArgument()->nextArg)]));
  }

/***************************************************************************
  NAME         : GetQueryInstanceSlot
  DESCRIPTION  : Internal function for referring to slots of instances in
                    instance array on instance-queries
  INPUTS       : The caller's result buffer
  RETURNS      : Nothing useful
  SIDE EFFECTS : Caller's result buffer set appropriately
  NOTES        : H/L Syntax : ((query-instance-slot) <index> <slot-name>)
 **************************************************************************/
globle void GetQueryInstanceSlot(
  void *theEnv,
  DATA_OBJECT *result)
  {
   INSTANCE_TYPE *ins;
   INSTANCE_SLOT *sp;
   DATA_OBJECT temp;
   QUERY_CORE *core;

   result->type = SYMBOL;
   result->value = SymbolData(theEnv)->FalseSymbol;

   core = FindQueryCore(theEnv,DOPToInteger(GetFirstArgument()));
   ins = core->solns[DOPToInteger(GetFirstArgument()->nextArg)];
   EvaluateExpression(theEnv,GetFirstArgument()->nextArg->nextArg,&temp);
   if (temp.type != SYMBOL)
     {
      ExpectedTypeError1(theEnv,"get",1,"symbol");
      SetEvaluationError(theEnv,TRUE);
      return;
     }
   sp = FindInstanceSlot(theEnv,ins,(SYMBOL_HN *) temp.value);
   if (sp == NULL)
     {
      SlotExistError(theEnv,ValueToString(temp.value),"instance-set query");
      return;
     }
   result->type = (unsigned short) sp->type;
   result->value = sp->value;
   if (sp->type == MULTIFIELD)
     {
      result->begin = 0;
      SetpDOEnd(result,GetInstanceSlotLength(sp));
     }
  }

/* =============================================================================
   =============================================================================
   Following are the instance query functions :

     any-instancep         : Determines if any instances satisfy the query
     find-instance         : Finds first (set of) instance(s) which satisfies
                               the query and stores it in a multi-field
     find-all-instances    : Finds all (sets of) instances which satisfy the
                               the query and stores them in a multi-field
     do-for-instance       : Executes a given action for the first (set of)
                               instance(s) which satisfy the query
     do-for-all-instances  : Executes an action for all instances which satisfy
                               the query as they are found
     delayed-do-for-all-instances : Same as above - except that the list of instances
                               which satisfy the query is formed before any
                               actions are executed

     Instance candidate search algorithm :

     All permutations of first restriction class instances with other
       restriction class instances (Rightmost are varied first)
     All permutations of first restriction class's subclasses' instances with
       other restriction class instances.
     And  so on...

     For any one class, instances are examined in the order they were defined

     Example :
     (defclass a (is-a standard-user))
     (defclass b (is-a standard-user))
     (defclass c (is-a standard-user))
     (defclass d (is-a a b))
     (make-instance a1 of a)
     (make-instance a2 of a)
     (make-instance b1 of b)
     (make-instance b2 of b)
     (make-instance c1 of c)
     (make-instance c2 of c)
     (make-instance d1 of d)
     (make-instance d2 of d)

     (any-instancep ((?a a b) (?b c)) <query>)

     The permutations (?a ?b) would be examined in the following order :

     (a1 c1),(a1 c2),(a2 c1),(a2 c2),(d1 c1),(d1 c2),(d2 c1),(d2 c2),
     (b1 c1),(b1 c2),(b2 c1),(b2 c2),(d1 c1),(d1 c2),(d2 c1),(d2 c2)

     Notice the duplication because d is a subclass of both and a and b.
   =============================================================================
   ============================================================================= */

/******************************************************************************
  NAME         : AnyInstances
  DESCRIPTION  : Determines if there any existing instances which satisfy
                   the query
  INPUTS       : None
  RETURNS      : TRUE if the query is satisfied, FALSE otherwise
  SIDE EFFECTS : The query class-expressions are evaluated once,
                   and the query boolean-expression is evaluated
                   zero or more times (depending on instance restrictions
                   and how early the expression evaulates to TRUE - if at all).
  NOTES        : H/L Syntax : See ParseQueryNoAction()
 ******************************************************************************/
globle BOOLEAN AnyInstances(
  void *theEnv)
  {
   QUERY_CLASS *qclasses;
   unsigned rcnt;
   int TestResult;

   qclasses = DetermineQueryClasses(theEnv,GetFirstArgument()->nextArg,
                                      "any-instancep",&rcnt);
   if (qclasses == NULL)
     return(FALSE);
   PushQueryCore(theEnv);
   InstanceQueryData(theEnv)->QueryCore = get_struct(theEnv,query_core);
   InstanceQueryData(theEnv)->QueryCore->solns = (INSTANCE_TYPE **) gm2(theEnv,(sizeof(INSTANCE_TYPE *) * rcnt));
   InstanceQueryData(theEnv)->QueryCore->query = GetFirstArgument();
   TestResult = TestForFirstInChain(theEnv,qclasses,0);
   InstanceQueryData(theEnv)->AbortQuery = FALSE;
   rm(theEnv,(void *) InstanceQueryData(theEnv)->QueryCore->solns,(sizeof(INSTANCE_TYPE *) * rcnt));
   rtn_struct(theEnv,query_core,InstanceQueryData(theEnv)->QueryCore);
   PopQueryCore(theEnv);
   DeleteQueryClasses(theEnv,qclasses);
   return(TestResult);
  }

/******************************************************************************
  NAME         : QueryFindInstance
  DESCRIPTION  : Finds the first set of instances which satisfy the query and
                   stores their names in the user's multi-field variable
  INPUTS       : Caller's result buffer
  RETURNS      : TRUE if the query is satisfied, FALSE otherwise
  SIDE EFFECTS : The query class-expressions are evaluated once,
                   and the query boolean-expression is evaluated
                   zero or more times (depending on instance restrictions
                   and how early the expression evaulates to TRUE - if at all).
  NOTES        : H/L Syntax : See ParseQueryNoAction()
 ******************************************************************************/
globle void QueryFindInstance(
  void *theEnv,
  DATA_OBJECT *result)
  {
   QUERY_CLASS *qclasses;
   unsigned rcnt,i;

   result->type = MULTIFIELD;
   result->begin = 0;
   result->end = -1;
   qclasses = DetermineQueryClasses(theEnv,GetFirstArgument()->nextArg,
                                      "find-instance",&rcnt);
   if (qclasses == NULL)
     {
      result->value = (void *) EnvCreateMultifield(theEnv,0L);
      return;
     }
   PushQueryCore(theEnv);
   InstanceQueryData(theEnv)->QueryCore = get_struct(theEnv,query_core);
   InstanceQueryData(theEnv)->QueryCore->solns = (INSTANCE_TYPE **)
                      gm2(theEnv,(sizeof(INSTANCE_TYPE *) * rcnt));
   InstanceQueryData(theEnv)->QueryCore->query = GetFirstArgument();
   if (TestForFirstInChain(theEnv,qclasses,0) == TRUE)
     {
      result->value = (void *) EnvCreateMultifield(theEnv,rcnt);
      SetpDOEnd(result,rcnt);
      for (i = 1 ; i <= rcnt ; i++)
        {
         SetMFType(result->value,i,INSTANCE_NAME);
         SetMFValue(result->value,i,GetFullInstanceName(theEnv,InstanceQueryData(theEnv)->QueryCore->solns[i - 1]));
        }
     }
   else
      result->value = (void *) EnvCreateMultifield(theEnv,0L);
   InstanceQueryData(theEnv)->AbortQuery = FALSE;
   rm(theEnv,(void *) InstanceQueryData(theEnv)->QueryCore->solns,(sizeof(INSTANCE_TYPE *) * rcnt));
   rtn_struct(theEnv,query_core,InstanceQueryData(theEnv)->QueryCore);
   PopQueryCore(theEnv);
   DeleteQueryClasses(theEnv,qclasses);
  }

/******************************************************************************
  NAME         : QueryFindAllInstances
  DESCRIPTION  : Finds all sets of instances which satisfy the query and
                   stores their names in the user's multi-field variable

                 The sets are stored sequentially :

                   Number of sets = (Multi-field length) / (Set length)

                 The first set is if the first (set length) atoms of the
                   multi-field variable, and so on.
  INPUTS       : Caller's result buffer
  RETURNS      : Nothing useful
  SIDE EFFECTS : The query class-expressions are evaluated once,
                   and the query boolean-expression is evaluated
                   once for every instance set.
  NOTES        : H/L Syntax : See ParseQueryNoAction()
 ******************************************************************************/
globle void QueryFindAllInstances(
  void *theEnv,
  DATA_OBJECT *result)
  {
   QUERY_CLASS *qclasses;
   unsigned rcnt;
   register unsigned i,j;

   EXPRESSION *theExpression;

   theExpression=GetFirstArgument();

   result->type = MULTIFIELD;
   result->begin = 0;
   result->end = -1;
   qclasses = DetermineQueryClasses(theEnv,GetFirstArgument()->nextArg,
                                      "find-all-instances",&rcnt);
   if (qclasses == NULL)
     {
      result->value = (void *) EnvCreateMultifield(theEnv,0L);
      return;
     }
   PushQueryCore(theEnv);
   InstanceQueryData(theEnv)->QueryCore = get_struct(theEnv,query_core);
   InstanceQueryData(theEnv)->QueryCore->solns = (INSTANCE_TYPE **) gm2(theEnv,(sizeof(INSTANCE_TYPE *) * rcnt));
   InstanceQueryData(theEnv)->QueryCore->query = GetFirstArgument();
   InstanceQueryData(theEnv)->QueryCore->action = NULL;
   InstanceQueryData(theEnv)->QueryCore->soln_set = NULL;
   InstanceQueryData(theEnv)->QueryCore->soln_size = rcnt;
   InstanceQueryData(theEnv)->QueryCore->soln_cnt = 0;
   TestEntireChain(theEnv,qclasses,0);
   InstanceQueryData(theEnv)->AbortQuery = FALSE;
   result->value = (void *) EnvCreateMultifield(theEnv,InstanceQueryData(theEnv)->QueryCore->soln_cnt * rcnt);
   while (InstanceQueryData(theEnv)->QueryCore->soln_set != NULL)
     {
      for (i = 0 , j = (unsigned) (result->end + 2) ; i < rcnt ; i++ , j++)
        {
         SetMFType(result->value,j,INSTANCE_NAME);
         SetMFValue(result->value,j,GetFullInstanceName(theEnv,InstanceQueryData(theEnv)->QueryCore->soln_set->soln[i]));
        }
      result->end = (long) j-2;
      PopQuerySoln(theEnv);
     }
   rm(theEnv,(void *) InstanceQueryData(theEnv)->QueryCore->solns,(sizeof(INSTANCE_TYPE *) * rcnt));
   rtn_struct(theEnv,query_core,InstanceQueryData(theEnv)->QueryCore);
   PopQueryCore(theEnv);
   DeleteQueryClasses(theEnv,qclasses);
  }

/******************************************************************************
  NAME         : QueryDoForInstance
  DESCRIPTION  : Finds the first set of instances which satisfy the query and
                   executes a user-action with that set
  INPUTS       : None
  RETURNS      : Caller's result buffer
  SIDE EFFECTS : The query class-expressions are evaluated once,
                   and the query boolean-expression is evaluated
                   zero or more times (depending on instance restrictions
                   and how early the expression evaulates to TRUE - if at all).
                   Also the action expression is executed zero or once.
                 Caller's result buffer holds result of user-action
  NOTES        : H/L Syntax : See ParseQueryAction()
 ******************************************************************************/
globle void QueryDoForInstance(
  void *theEnv,
  DATA_OBJECT *result)
  {
   QUERY_CLASS *qclasses;
   unsigned rcnt;

   result->type = SYMBOL;
   result->value = SymbolData(theEnv)->FalseSymbol;
   qclasses = DetermineQueryClasses(theEnv,GetFirstArgument()->nextArg->nextArg,
                                      "do-for-instance",&rcnt);
   if (qclasses == NULL)
     return;
   PushQueryCore(theEnv);
   InstanceQueryData(theEnv)->QueryCore = get_struct(theEnv,query_core);
   InstanceQueryData(theEnv)->QueryCore->solns = (INSTANCE_TYPE **) gm2(theEnv,(sizeof(INSTANCE_TYPE *) * rcnt));
   InstanceQueryData(theEnv)->QueryCore->query = GetFirstArgument();
   InstanceQueryData(theEnv)->QueryCore->action = GetFirstArgument()->nextArg;
   if (TestForFirstInChain(theEnv,qclasses,0) == TRUE)
     EvaluateExpression(theEnv,InstanceQueryData(theEnv)->QueryCore->action,result);
   InstanceQueryData(theEnv)->AbortQuery = FALSE;
   ProcedureFunctionData(theEnv)->BreakFlag = FALSE;
   rm(theEnv,(void *) InstanceQueryData(theEnv)->QueryCore->solns,(sizeof(INSTANCE_TYPE *) * rcnt));
   rtn_struct(theEnv,query_core,InstanceQueryData(theEnv)->QueryCore);
   PopQueryCore(theEnv);
   DeleteQueryClasses(theEnv,qclasses);
  }

/******************************************************************************
  NAME         : QueryDoForAllInstances
  DESCRIPTION  : Finds all sets of instances which satisfy the query and
                   executes a user-function for each set as it is found
  INPUTS       : Caller's result buffer
  RETURNS      : Nothing useful
  SIDE EFFECTS : The query class-expressions are evaluated once,
                   and the query boolean-expression is evaluated
                   once for every instance set.  Also, the action is
                   executed for every instance set.
                 Caller's result buffer holds result of last action executed.
  NOTES        : H/L Syntax : See ParseQueryAction()
 ******************************************************************************/
globle void QueryDoForAllInstances(
  void *theEnv,
  DATA_OBJECT *result)
  {
   QUERY_CLASS *qclasses;
   unsigned rcnt;

   result->type = SYMBOL;
   result->value = SymbolData(theEnv)->FalseSymbol;
   qclasses = DetermineQueryClasses(theEnv,GetFirstArgument()->nextArg->nextArg,
                                      "do-for-all-instances",&rcnt);
   if (qclasses == NULL)
     return;
   PushQueryCore(theEnv);
   InstanceQueryData(theEnv)->QueryCore = get_struct(theEnv,query_core);
   InstanceQueryData(theEnv)->QueryCore->solns = (INSTANCE_TYPE **) gm2(theEnv,(sizeof(INSTANCE_TYPE *) * rcnt));
   InstanceQueryData(theEnv)->QueryCore->query = GetFirstArgument();
   InstanceQueryData(theEnv)->QueryCore->action = GetFirstArgument()->nextArg;
   InstanceQueryData(theEnv)->QueryCore->result = result;
   ValueInstall(theEnv,InstanceQueryData(theEnv)->QueryCore->result);
   TestEntireChain(theEnv,qclasses,0);
   ValueDeinstall(theEnv,InstanceQueryData(theEnv)->QueryCore->result);
   PropagateReturnValue(theEnv,InstanceQueryData(theEnv)->QueryCore->result);
   InstanceQueryData(theEnv)->AbortQuery = FALSE;
   ProcedureFunctionData(theEnv)->BreakFlag = FALSE;
   rm(theEnv,(void *) InstanceQueryData(theEnv)->QueryCore->solns,(sizeof(INSTANCE_TYPE *) * rcnt));
   rtn_struct(theEnv,query_core,InstanceQueryData(theEnv)->QueryCore);
   PopQueryCore(theEnv);
   DeleteQueryClasses(theEnv,qclasses);
  }

/******************************************************************************
  NAME         : DelayedQueryDoForAllInstances
  DESCRIPTION  : Finds all sets of instances which satisfy the query and
                   and exceutes a user-action for each set

                 This function differs from QueryDoForAllInstances() in
                   that it forms the complete list of query satisfactions
                   BEFORE executing any actions.
  INPUTS       : Caller's result buffer
  RETURNS      : Nothing useful
  SIDE EFFECTS : The query class-expressions are evaluated once,
                   and the query boolean-expression is evaluated
                   once for every instance set.  The action is executed
                   for evry query satisfaction.
                 Caller's result buffer holds result of last action executed.
  NOTES        : H/L Syntax : See ParseQueryNoAction()
 ******************************************************************************/
globle void DelayedQueryDoForAllInstances(
  void *theEnv,
  DATA_OBJECT *result)
  {
   QUERY_CLASS *qclasses;
   unsigned rcnt;
   register unsigned i;

   result->type = SYMBOL;
   result->value = SymbolData(theEnv)->FalseSymbol;
   qclasses = DetermineQueryClasses(theEnv,GetFirstArgument()->nextArg->nextArg,
                                      "delayed-do-for-all-instances",&rcnt);
   if (qclasses == NULL)
     return;
   PushQueryCore(theEnv);
   InstanceQueryData(theEnv)->QueryCore = get_struct(theEnv,query_core);
   InstanceQueryData(theEnv)->QueryCore->solns = (INSTANCE_TYPE **) gm2(theEnv,(sizeof(INSTANCE_TYPE *) * rcnt));
   InstanceQueryData(theEnv)->QueryCore->query = GetFirstArgument();
   InstanceQueryData(theEnv)->QueryCore->action = NULL;
   InstanceQueryData(theEnv)->QueryCore->soln_set = NULL;
   InstanceQueryData(theEnv)->QueryCore->soln_size = rcnt;
   InstanceQueryData(theEnv)->QueryCore->soln_cnt = 0;
   TestEntireChain(theEnv,qclasses,0);
   InstanceQueryData(theEnv)->AbortQuery = FALSE;
   InstanceQueryData(theEnv)->QueryCore->action = GetFirstArgument()->nextArg;
   while (InstanceQueryData(theEnv)->QueryCore->soln_set != NULL)
     {
      for (i = 0 ; i < rcnt ; i++)
        InstanceQueryData(theEnv)->QueryCore->solns[i] = InstanceQueryData(theEnv)->QueryCore->soln_set->soln[i];
      PopQuerySoln(theEnv);
      EvaluationData(theEnv)->CurrentEvaluationDepth++;
      EvaluateExpression(theEnv,InstanceQueryData(theEnv)->QueryCore->action,result);
      EvaluationData(theEnv)->CurrentEvaluationDepth--;
      if (ProcedureFunctionData(theEnv)->ReturnFlag == TRUE)
        { PropagateReturnValue(theEnv,result); }
      PeriodicCleanup(theEnv,FALSE,TRUE);
      if (EvaluationData(theEnv)->HaltExecution || ProcedureFunctionData(theEnv)->BreakFlag || ProcedureFunctionData(theEnv)->ReturnFlag)
        {
         while (InstanceQueryData(theEnv)->QueryCore->soln_set != NULL)
           PopQuerySoln(theEnv);
         break;
        }
     }
   ProcedureFunctionData(theEnv)->BreakFlag = FALSE;
   rm(theEnv,(void *) InstanceQueryData(theEnv)->QueryCore->solns,(sizeof(INSTANCE_TYPE *) * rcnt));
   rtn_struct(theEnv,query_core,InstanceQueryData(theEnv)->QueryCore);
   PopQueryCore(theEnv);
   DeleteQueryClasses(theEnv,qclasses);
  }

/* =========================================
   *****************************************
          INTERNALLY VISIBLE FUNCTIONS
   =========================================
   ***************************************** */

/*******************************************************
  NAME         : PushQueryCore
  DESCRIPTION  : Pushes the current QueryCore onto stack
  INPUTS       : None
  RETURNS      : Nothing useful
  SIDE EFFECTS : Allocates new stack node and changes
                   QueryCoreStack
  NOTES        : None
 *******************************************************/
static void PushQueryCore(
  void *theEnv)
  {
   QUERY_STACK *qptr;

   qptr = get_struct(theEnv,query_stack);
   qptr->core = InstanceQueryData(theEnv)->QueryCore;
   qptr->nxt = InstanceQueryData(theEnv)->QueryCoreStack;
   InstanceQueryData(theEnv)->QueryCoreStack = qptr;
  }

/******************************************************
  NAME         : PopQueryCore
  DESCRIPTION  : Pops top of QueryCore stack and
                   restores QueryCore to this core
  INPUTS       : None
  RETURNS      : Nothing useful
  SIDE EFFECTS : Stack node deallocated, QueryCoreStack
                   changed and QueryCore reset
  NOTES        : Assumes stack is not empty
 ******************************************************/
static void PopQueryCore(
  void *theEnv)
  {
   QUERY_STACK *qptr;

   InstanceQueryData(theEnv)->QueryCore = InstanceQueryData(theEnv)->QueryCoreStack->core;
   qptr = InstanceQueryData(theEnv)->QueryCoreStack;
   InstanceQueryData(theEnv)->QueryCoreStack = InstanceQueryData(theEnv)->QueryCoreStack->nxt;
   rtn_struct(theEnv,query_stack,qptr);
  }

/***************************************************
  NAME         : FindQueryCore
  DESCRIPTION  : Looks up a QueryCore Stack Frame
                    Depth 0 is current frame
                    1 is next deepest, etc.
  INPUTS       : Depth
  RETURNS      : Address of query core stack frame
  SIDE EFFECTS : None
  NOTES        : None
 ***************************************************/
static QUERY_CORE *FindQueryCore(
  void *theEnv,
  int depth)
  {
   QUERY_STACK *qptr;

   if (depth == 0)
     return(InstanceQueryData(theEnv)->QueryCore);
   qptr = InstanceQueryData(theEnv)->QueryCoreStack;
   while (depth > 1)
     {
      qptr = qptr->nxt;
      depth--;
     }
   return(qptr->core);
  }

/**********************************************************
  NAME         : DetermineQueryClasses
  DESCRIPTION  : Builds a list of classes to be used in
                   instance queries - uses parse form.
  INPUTS       : 1) The parse class expression chain
                 2) The name of the function being executed
                 3) Caller's buffer for restriction count
                    (# of separate lists)
  RETURNS      : The query list, or NULL on errors
  SIDE EFFECTS : Memory allocated for list
                 Busy count incremented for all classes
  NOTES        : Each restriction is linked by nxt pointer,
                   multiple classes in a restriction are
                   linked by the chain pointer.
                 Rcnt caller's buffer is set to reflect the
                   total number of chains
                 Assumes classExp is not NULL and that each
                   restriction chain is terminated with
                   the QUERY_DELIMITER_SYMBOL "(QDS)"
 **********************************************************/
static QUERY_CLASS *DetermineQueryClasses(
  void *theEnv,
  EXPRESSION *classExp,
  char *func,
  unsigned *rcnt)
  {
   QUERY_CLASS *clist = NULL,*cnxt = NULL,*cchain = NULL,*tmp;
   int new_list = FALSE;
   DATA_OBJECT temp;

   *rcnt = 0;
   while (classExp != NULL)
     {
      if (EvaluateExpression(theEnv,classExp,&temp))
        {
         DeleteQueryClasses(theEnv,clist);
         return(NULL);
        }
      if ((temp.type == SYMBOL) && (temp.value == (void *) InstanceQueryData(theEnv)->QUERY_DELIMETER_SYMBOL))
        {
         new_list = TRUE;
         (*rcnt)++;
        }
      else if ((tmp = FormChain(theEnv,func,&temp)) != NULL)
        {
         if (clist == NULL)
           clist = cnxt = cchain = tmp;
         else if (new_list == TRUE)
           {
            new_list = FALSE;
            cnxt->nxt = tmp;
            cnxt = cchain = tmp;
           }
         else
           cchain->chain = tmp;
         while (cchain->chain != NULL)
           cchain = cchain->chain;
        }
      else
        {
         SyntaxErrorMessage(theEnv,"instance-set query class restrictions");
         DeleteQueryClasses(theEnv,clist);
         SetEvaluationError(theEnv,TRUE);
         return(NULL);
        }
      classExp = classExp->nextArg;
     }
   return(clist);
  }

/*************************************************************
  NAME         : FormChain
  DESCRIPTION  : Builds a list of classes to be used in
                   instance queries - uses parse form.
  INPUTS       : 1) Name of calling function for error msgs
                 2) Data object - must be a symbol or a
                      multifield value containing all symbols
                 The symbols must be names of existing classes
  RETURNS      : The query chain, or NULL on errors
  SIDE EFFECTS : Memory allocated for chain
                 Busy count incremented for all classes
  NOTES        : None
 *************************************************************/
static QUERY_CLASS *FormChain(
  void *theEnv,
  char *func,
  DATA_OBJECT *val)
  {
   DEFCLASS *cls;
   QUERY_CLASS *head,*bot,*tmp;
   register long i,end; /* 6.04 Bug Fix */
   char *className;
   struct defmodule *currentModule;

   currentModule = ((struct defmodule *) EnvGetCurrentModule(theEnv));
   if (val->type == DEFCLASS_PTR)
     {
      IncrementDefclassBusyCount(theEnv,(void *) val->value);
      head = get_struct(theEnv,query_class);
      head->cls = (DEFCLASS *) val->value;
      if (DefclassInScope(theEnv,head->cls,currentModule))
        head->theModule = currentModule;
      else
        head->theModule = head->cls->header.whichModule->theModule;
      head->chain = NULL;
      head->nxt = NULL;
      return(head);
     }
   if (val->type == SYMBOL)
     {
      /* ===============================================
         Allow instance-set query restrictions to have a
         module specifier as part of the class name,
         but search imported defclasses too if a
         module specifier is not given
         =============================================== */
      cls = LookupDefclassByMdlOrScope(theEnv,DOPToString(val));
      if (cls == NULL)
        {
         ClassExistError(theEnv,func,DOPToString(val));
         return(NULL);
        }
      IncrementDefclassBusyCount(theEnv,(void *) cls);
      head = get_struct(theEnv,query_class);
      head->cls = cls;
      if (DefclassInScope(theEnv,head->cls,currentModule))
        head->theModule = currentModule;
      else
        head->theModule = head->cls->header.whichModule->theModule;
      head->chain = NULL;
      head->nxt = NULL;
      return(head);
     }
   if (val->type == MULTIFIELD)
     {
      head = bot = NULL;
      end = GetpDOEnd(val);
      for (i = GetpDOBegin(val) ; i <= end ; i++)
        {
         if (GetMFType(val->value,i) == SYMBOL)
           {
            className = ValueToString(GetMFValue(val->value,i));
            cls = LookupDefclassByMdlOrScope(theEnv,className);
            if (cls == NULL)
              {
               ClassExistError(theEnv,func,className);
               DeleteQueryClasses(theEnv,head);
               return(NULL);
              }
           }
         else
           {
            DeleteQueryClasses(theEnv,head);
            return(NULL);
           }
         IncrementDefclassBusyCount(theEnv,(void *) cls);
         tmp = get_struct(theEnv,query_class);
         tmp->cls = cls;
         if (DefclassInScope(theEnv,tmp->cls,currentModule))
           tmp->theModule = currentModule;
         else
           tmp->theModule = tmp->cls->header.whichModule->theModule;
         tmp->chain = NULL;
         tmp->nxt = NULL;
         if (head == NULL)
           head = tmp;
         else
           bot->chain = tmp;
         bot = tmp;
        }
      return(head);
     }
   return(NULL);
  }

/******************************************************
  NAME         : DeleteQueryClasses
  DESCRIPTION  : Deletes a query class-list
  INPUTS       : The query list address
  RETURNS      : Nothing useful
  SIDE EFFECTS : Nodes deallocated
                 Busy count decremented for all classes
  NOTES        : None
 ******************************************************/
static void DeleteQueryClasses(
  void *theEnv,
  QUERY_CLASS *qlist)
  {
   QUERY_CLASS *tmp;

   while (qlist != NULL)
     {
      while (qlist->chain != NULL)
        {
         tmp = qlist->chain;
         qlist->chain = qlist->chain->chain;
         DecrementDefclassBusyCount(theEnv,(void *) tmp->cls);
         rtn_struct(theEnv,query_class,tmp);
        }
      tmp = qlist;
      qlist = qlist->nxt;
      DecrementDefclassBusyCount(theEnv,(void *) tmp->cls);
      rtn_struct(theEnv,query_class,tmp);
     }
  }

/************************************************************
  NAME         : TestForFirstInChain
  DESCRIPTION  : Processes all classes in a restriction chain
                   until success or done
  INPUTS       : 1) The current chain
                 2) The index of the chain restriction
                     (e.g. the 4th query-variable)
  RETURNS      : TRUE if query succeeds, FALSE otherwise
  SIDE EFFECTS : Sets current restriction class
                 Instance variable values set
  NOTES        : None
 ************************************************************/
static int TestForFirstInChain(
  void *theEnv,
  QUERY_CLASS *qchain,
  int indx)
  {
   QUERY_CLASS *qptr;
   int id;

   InstanceQueryData(theEnv)->AbortQuery = TRUE;
   for (qptr = qchain ; qptr != NULL ; qptr = qptr->chain)
     {
      InstanceQueryData(theEnv)->AbortQuery = FALSE;
      if ((id = GetTraversalID(theEnv)) == -1)
        return(FALSE);
      if (TestForFirstInstanceInClass(theEnv,qptr->theModule,id,qptr->cls,qchain,indx))
        {
         ReleaseTraversalID(theEnv);
         return(TRUE);
        }
      ReleaseTraversalID(theEnv);
      if ((EvaluationData(theEnv)->HaltExecution == TRUE) || (InstanceQueryData(theEnv)->AbortQuery == TRUE))
        return(FALSE);
     }
   return(FALSE);
  }

/*****************************************************************
  NAME         : TestForFirstInstanceInClass
  DESCRIPTION  : Processes all instances in a class and then
                   all subclasses of a class until success or done
  INPUTS       : 1) The module for which classes tested must be
                    in scope
                 2) Visitation traversal id
                 3) The class
                 4) The current class restriction chain
                 5) The index of the current restriction
  RETURNS      : TRUE if query succeeds, FALSE otherwise
  SIDE EFFECTS : Instance variable values set
  NOTES        : None
 *****************************************************************/
static int TestForFirstInstanceInClass(
  void *theEnv,
  struct defmodule *theModule,
  int id,
  DEFCLASS *cls,
  QUERY_CLASS *qchain,
  int indx)
  {
   register unsigned i;
   INSTANCE_TYPE *ins;
   DATA_OBJECT temp;

   if (TestTraversalID(cls->traversalRecord,id))
     return(FALSE);
   SetTraversalID(cls->traversalRecord,id);
   if (DefclassInScope(theEnv,cls,theModule) == FALSE)
     return(FALSE);
   ins = cls->instanceList;
   while (ins != NULL)
     {
      InstanceQueryData(theEnv)->QueryCore->solns[indx] = ins;
      if (qchain->nxt != NULL)
        {
         ins->busy++;
         if (TestForFirstInChain(theEnv,qchain->nxt,indx+1) == TRUE)
           {
            ins->busy--;
            break;
           }
         ins->busy--;
         if ((EvaluationData(theEnv)->HaltExecution == TRUE) || (InstanceQueryData(theEnv)->AbortQuery == TRUE))
           break;
        }
      else
        {
         ins->busy++;
         EvaluationData(theEnv)->CurrentEvaluationDepth++;
         EvaluateExpression(theEnv,InstanceQueryData(theEnv)->QueryCore->query,&temp);
         EvaluationData(theEnv)->CurrentEvaluationDepth--;
         PeriodicCleanup(theEnv,FALSE,TRUE);
         ins->busy--;
         if (EvaluationData(theEnv)->HaltExecution == TRUE)
           break;
         if ((temp.type != SYMBOL) ? TRUE :
             (temp.value != SymbolData(theEnv)->FalseSymbol))
           break;
        }
      ins = ins->nxtClass;
      while ((ins != NULL) ? (ins->garbage == 1) : FALSE)
        ins = ins->nxtClass;
     }
   if (ins != NULL)
     return(((EvaluationData(theEnv)->HaltExecution == TRUE) || (InstanceQueryData(theEnv)->AbortQuery == TRUE))
             ? FALSE : TRUE);
   for (i = 0 ; i < cls->directSubclasses.classCount ; i++)
     {
      if (TestForFirstInstanceInClass(theEnv,theModule,id,cls->directSubclasses.classArray[i],
                                      qchain,indx))
        return(TRUE);
      if ((EvaluationData(theEnv)->HaltExecution == TRUE) || (InstanceQueryData(theEnv)->AbortQuery == TRUE))
        return(FALSE);
     }
   return(FALSE);
  }

/************************************************************
  NAME         : TestEntireChain
  DESCRIPTION  : Processes all classes in a restriction chain
                   until done
  INPUTS       : 1) The current chain
                 2) The index of the chain restriction
                     (i.e. the 4th query-variable)
  RETURNS      : Nothing useful
  SIDE EFFECTS : Sets current restriction class
                 Query instance variables set
                 Solution sets stored in global list
  NOTES        : None
 ************************************************************/
static void TestEntireChain(
  void *theEnv,
  QUERY_CLASS *qchain,
  int indx)
  {
   QUERY_CLASS *qptr;
   int id;

   InstanceQueryData(theEnv)->AbortQuery = TRUE;
   for (qptr = qchain ; qptr != NULL ; qptr = qptr->chain)
     {
      InstanceQueryData(theEnv)->AbortQuery = FALSE;
      if ((id = GetTraversalID(theEnv)) == -1)
        return;
      TestEntireClass(theEnv,qptr->theModule,id,qptr->cls,qchain,indx);
      ReleaseTraversalID(theEnv);
      if ((EvaluationData(theEnv)->HaltExecution == TRUE) || (InstanceQueryData(theEnv)->AbortQuery == TRUE))
        return;
     }
  }

/*****************************************************************
  NAME         : TestEntireClass
  DESCRIPTION  : Processes all instances in a class and then
                   all subclasses of a class until done
  INPUTS       : 1) The module for which classes tested must be
                    in scope
                 2) Visitation traversal id
                 3) The class
                 4) The current class restriction chain
                 5) The index of the current restriction
  RETURNS      : Nothing useful
  SIDE EFFECTS : Instance variable values set
                 Solution sets stored in global list
  NOTES        : None
 *****************************************************************/
static void TestEntireClass(
  void *theEnv,
  struct defmodule *theModule,
  int id,
  DEFCLASS *cls,
  QUERY_CLASS *qchain,
  int indx)
  {
   register unsigned i;
   INSTANCE_TYPE *ins;
   DATA_OBJECT temp;

   if (TestTraversalID(cls->traversalRecord,id))
     return;
   SetTraversalID(cls->traversalRecord,id);
   if (DefclassInScope(theEnv,cls,theModule) == FALSE)
     return;
   ins = cls->instanceList;
   while (ins != NULL)
     {
      InstanceQueryData(theEnv)->QueryCore->solns[indx] = ins;
      if (qchain->nxt != NULL)
        {
         ins->busy++;
         TestEntireChain(theEnv,qchain->nxt,indx+1);
         ins->busy--;
         if ((EvaluationData(theEnv)->HaltExecution == TRUE) || (InstanceQueryData(theEnv)->AbortQuery == TRUE))
           break;
        }
      else
        {
         ins->busy++;
         EvaluationData(theEnv)->CurrentEvaluationDepth++;
         EvaluateExpression(theEnv,InstanceQueryData(theEnv)->QueryCore->query,&temp);
         EvaluationData(theEnv)->CurrentEvaluationDepth--;
         PeriodicCleanup(theEnv,FALSE,TRUE);
         ins->busy--;
         if (EvaluationData(theEnv)->HaltExecution == TRUE)
           break;
         if ((temp.type != SYMBOL) ? TRUE :
             (temp.value != SymbolData(theEnv)->FalseSymbol))
           {
            if (InstanceQueryData(theEnv)->QueryCore->action != NULL)
              {
               ins->busy++;
               EvaluationData(theEnv)->CurrentEvaluationDepth++;
               ValueDeinstall(theEnv,InstanceQueryData(theEnv)->QueryCore->result);
               EvaluateExpression(theEnv,InstanceQueryData(theEnv)->QueryCore->action,InstanceQueryData(theEnv)->QueryCore->result);
               ValueInstall(theEnv,InstanceQueryData(theEnv)->QueryCore->result);
               EvaluationData(theEnv)->CurrentEvaluationDepth--;
               PeriodicCleanup(theEnv,FALSE,TRUE);
               ins->busy--;
               if (ProcedureFunctionData(theEnv)->BreakFlag || ProcedureFunctionData(theEnv)->ReturnFlag)
                 {
                  InstanceQueryData(theEnv)->AbortQuery = TRUE;
                  break;
                 }
               if (EvaluationData(theEnv)->HaltExecution == TRUE)
                 break;
              }
            else
              AddSolution(theEnv);
           }
        }
        
      ins = ins->nxtClass;
      while ((ins != NULL) ? (ins->garbage == 1) : FALSE)
        ins = ins->nxtClass;
     }
   if (ins != NULL)
     return;
   for (i = 0 ; i < cls->directSubclasses.classCount ; i++)
     {
      TestEntireClass(theEnv,theModule,id,cls->directSubclasses.classArray[i],qchain,indx);
      if ((EvaluationData(theEnv)->HaltExecution == TRUE) || (InstanceQueryData(theEnv)->AbortQuery == TRUE))
        return;
     }
  }

/***************************************************************************
  NAME         : AddSolution
  DESCRIPTION  : Adds the current instance set to a global list of
                   solutions
  INPUTS       : None
  RETURNS      : Nothing useful
  SIDE EFFECTS : Global list and count updated
  NOTES        : Solutions are stored as sequential arrays of INSTANCE_TYPE *
 ***************************************************************************/
static void AddSolution(
  void *theEnv)
  {
   QUERY_SOLN *new_soln;
   register unsigned i;

   new_soln = (QUERY_SOLN *) gm2(theEnv,(int) sizeof(QUERY_SOLN));
   new_soln->soln = (INSTANCE_TYPE **)
                    gm2(theEnv,(sizeof(INSTANCE_TYPE *) * (InstanceQueryData(theEnv)->QueryCore->soln_size)));
   for (i = 0 ; i < InstanceQueryData(theEnv)->QueryCore->soln_size ; i++)
     new_soln->soln[i] = InstanceQueryData(theEnv)->QueryCore->solns[i];
   new_soln->nxt = NULL;
   if (InstanceQueryData(theEnv)->QueryCore->soln_set == NULL)
     InstanceQueryData(theEnv)->QueryCore->soln_set = new_soln;
   else
     InstanceQueryData(theEnv)->QueryCore->soln_bottom->nxt = new_soln;
   InstanceQueryData(theEnv)->QueryCore->soln_bottom = new_soln;
   InstanceQueryData(theEnv)->QueryCore->soln_cnt++;
  }

/***************************************************
  NAME         : PopQuerySoln
  DESCRIPTION  : Deallocates the topmost solution
                   set for an instance-set query
  INPUTS       : None
  RETURNS      : Nothing useful
  SIDE EFFECTS : Solution set deallocated
  NOTES        : Assumes QueryCore->soln_set != 0
 ***************************************************/
static void PopQuerySoln(
  void *theEnv)
  {
   InstanceQueryData(theEnv)->QueryCore->soln_bottom = InstanceQueryData(theEnv)->QueryCore->soln_set;
   InstanceQueryData(theEnv)->QueryCore->soln_set = InstanceQueryData(theEnv)->QueryCore->soln_set->nxt;
   rm(theEnv,(void *) InstanceQueryData(theEnv)->QueryCore->soln_bottom->soln,
      (sizeof(INSTANCE_TYPE *) * InstanceQueryData(theEnv)->QueryCore->soln_size));
   rm(theEnv,(void *) InstanceQueryData(theEnv)->QueryCore->soln_bottom,sizeof(QUERY_SOLN));
  }

#endif


