;(defclass PREGAO (is-a USER) (slot Timestamp))
;(defclass COTACAO (is-a USER) (slot Timestamp) (slot Pregao) (slot Papel)(slot Fechamento))
;(defclass PAPEL (is-a USER) (slot Timestamp))
;(defclass TECHNICALANALISYS (is-a USER) (slot Timestamp) (slot Pregao)(slot Papel)(slot Valor))
;(defclass NOVOPORTFOLIO (is-a USER) (slot Timestamp) (slot Pregao) (slot Papel) (slot RetornoEsperado)(slot RiscoEsperado))
;(defclass NOVOPAPELEMPORTFOLIO (is-a USER) (slot Porcentagem) (slot Timestamp) (slot Pregao) (slot Papel) (slot RetornoEsperado))
;(definstances TESTE2 
;   (Papel1 of PAPEL (Timestamp 100))
;   (Papel2 of PAPEL (Timestamp 101))
;  (Papel3 of PAPEL (Timestamp 102))
;  (Pregao4 of PREGAO (Timestamp 200))
;   (Pregao5 of PREGAO (Timestamp 201))
;   (Pregao6 of PREGAO (Timestamp 202))
;   (Tech1 of TECHNICALANALISYS (Timestamp 300)(Pregao 200)(Papel 100))
;   (Tech2 of TECHNICALANALISYS (Timestamp 300)(Pregao 201)(Papel 100))
;   (Tech3 of TECHNICALANALISYS (Timestamp 300)(Pregao 202)(Papel 100))
;   (Tech4 of TECHNICALANALISYS (Timestamp 300)(Pregao 200)(Papel 101))
;   (Tech5 of TECHNICALANALISYS (Timestamp 300)(Pregao 201)(Papel 101))
;   (Tech6 of TECHNICALANALISYS (Timestamp 300)(Pregao 202)(Papel 101))
;  (Tech7 of TECHNICALANALISYS (Timestamp 300)(Pregao 200)(Papel 102))
;   (Tech8 of TECHNICALANALISYS (Timestamp 300)(Pregao 201)(Papel 102))
;  (Tech9 of TECHNICALANALISYS (Timestamp 300)(Pregao 202)(Papel 102))
;   (Cotacao4 of COTACAO (Timestamp 201)(Pregao 200)(Papel 100)(Fechamento 45.4))
;   (Cotacao5 of COTACAO (Timestamp 201)(Pregao 200)(Papel 101)(Fechamento 55.4))
;   (Cotacao6 of COTACAO (Timestamp 201)(Pregao 200)(Papel 102)(Fechamento 65.4))
;  (Cotacao7 of COTACAO (Timestamp 201)(Pregao 201)(Papel 100)(Fechamento 76.4))
;   (Cotacao8 of COTACAO (Timestamp 201)(Pregao 201)(Papel 101)(Fechamento 65.4))
;   (Cotacao9 of COTACAO (Timestamp 201)(Pregao 201)(Papel 102)(Fechamento 45.4))
;   (Cotacao10 of COTACAO (Timestamp 201)(Pregao 202)(Papel 100)(Fechamento 23.4))
;   (Cotacao11 of COTACAO (Timestamp 201)(Pregao 202)(Papel 101)(Fechamento 45.4))
;   (Cotacao12 of COTACAO (Timestamp 201)(Pregao 202)(Papel 102)(Fechamento 23.4))
;)

(defrule AdicionarPapel
    (declare (salience 1000))
    ?NovoPapel <- (object (is-a PAPEL) 
                               (Timestamp  ?Timest))
=>
    (bind ?t (str-cat ?Timest))
    (AddPapel ?t)
)

(defrule AdicionarQuote
    ?NovaCotacao <- (object (is-a COTACAO) 
                               (Papel  ?Papel2)
                               (Pregao  ?Preg)
                               (Fechamento  ?Fech))
=>
    (bind ?t1 (str-cat ?Papel2))
    (bind ?t2 (str-cat ?Preg))
    (bind ?t3 (str-cat ?Fech))
    (AddQuote ?t1 ?t2 ?t3)
)

(defrule AdicionarTechnical
    (declare (salience -20))
    ?NovaCotacao <- (object (is-a TECHNICALANALISYS) (Pregao ?Pr)(Valor ?Val)(Papel ?Pap))
=>
    (bind ?tr (str-cat "Technical:"?Pr))
    (LogCLIPS ?tr)    
    (bind ?t1 (str-cat ?Pr))
    (bind ?t2 (str-cat ?Val))
    (bind ?t3 (str-cat ?Pap))
    (AddEstimativaRetorno ?t3 ?t1 ?t2)
)

(defrule AdicionarTechnical2
    ?NovaCotacao <- (object (is-a PREGAO) (Timestamp ?Pr))
=>
    (bind ?tr (str-cat "Pregao:"?Pr))
    (LogCLIPS ?tr)    
)


(defrule ChecarNovoPregao 
    ?t3 <- (object (is-a PREGAO)(Timestamp ?t))
=>
    (bind ?ContagemPregoes 0)
    (do-for-all-instances ((?ins PREGAO)) (= 1 1)
         (bind ?ContagemPregoes (+ ?ContagemPregoes 1))
    )
    (bind ?t1 (str-cat "Pregoes:" ?ContagemPregoes ))
    (LogCLIPS ?t1)    
    (if (> ?ContagemPregoes 25) then
        (assert (DevoAnalisar ?t))
    )
)

(defrule VerificarDados
       (declare (salience -1000))
       ?Novo <- (DevoAnalisar ?t)
       ?tech3 <- (object (is-a TECHNICALANALISYS)(Pregao ?t))
=> 
    (bind ?ContagemPapeis 0)
    (bind ?ContagemAnalisesTecnicas 0)
    (bind ?ContagemCotacoes 0)
    (bind ?ContagemNovoPort 0)

    (do-for-all-instances ((?ins PAPEL)) (= 1 1)
         (bind ?ContagemPapeis (+ ?ContagemPapeis 1))
    )
    (do-for-all-instances ((?ins2 TECHNICALANALISYS)) (= ?ins2:Pregao ?t )
         (bind ?ContagemAnalisesTecnicas (+ ?ContagemAnalisesTecnicas 1))
    )
    (do-for-all-instances ((?ins3 COTACAO)) (= ?ins3:Pregao ?t )
         (bind ?ContagemCotacoes (+ ?ContagemCotacoes 1))
    )
    (do-for-all-instances ((?ins4 NOVOPORTFOLIO)) (= ?ins4:Pregao ?t )
         (bind ?ContagemNovoPort (+ ?ContagemNovoPort 1))
    )
    (if (= ?ContagemNovoPort 0) then
    (if (and (= ?ContagemPapeis ?ContagemAnalisesTecnicas) (= ?ContagemPapeis ?ContagemCotacoes)) then
        (bind ?tr (str-cat  "Okay" ?ContagemPapeis ":" ?ContagemAnalisesTecnicas ":" ?ContagemCotacoes))
        (LogCLIPS ?tr)
        (printout t "Okay, Calculando Markowitz..." crlf) 
        (bind ?tr3 (str-cat ?t))
        (CalculateMarkowitz ?tr3 5 4)        
     else
        (bind ?tr (str-cat  "NOK" ?ContagemPapeis ":" ?ContagemAnalisesTecnicas ":" ?ContagemCotacoes))
        (LogCLIPS ?tr)
        (retract ?Novo)
        (assert (DevoAnalisar ?t))
    )
    )
)

(defrule TesteNovoPortfolio
    ?NovoPor <- (object (is-a NOVOPORTFOLIO) 
                        (RetornoEsperado ?RetEsp)(Pregao ?Preg)(RiscoEsperado ?Ris))
=>
    (printout t "NovoPortfolio, Retorno:" ?RetEsp " para Pregao:" ?Preg crlf)
    (bind ?Str (str-cat "RiscoEsperado=0;RetornoEsperado=" ?RetEsp ";Pregao=" ?Preg ";DevoComprar=0;ValorDinheiro=0;Valor=0"))
    (WriteToBlackboard "NovoPortfolio" ?Str)
    
    (do-for-all-instances ((?ins3 NOVOPAPELEMPORTFOLIO)) (= ?ins3:Pregao ?Preg )
         (bind ?Str2 (str-cat "Porcentagem=" ?ins3:Porcentagem ";Pregao=" ?ins3:Pregao ";Papel=" ?ins3:Papel )   )
         (WriteToBlackboard "NovoPapelEmPortfolio" ?Str2)
    )

    (do-for-all-instances ((?ins4 NOVOPAPELEMPORTFOLIO)) (= ?ins4:Pregao ?Preg )
        (unmake-instance ?ins4)
    )
    (do-for-all-instances ((?ins5 TECHNICALANALISYS)) (< ?ins5:Pregao ?Preg )
        (unmake-instance ?ins5)
    )
    (do-for-all-instances ((?ins6 COTACAO)) (< ?ins6:Pregao ?Preg )
        (unmake-instance ?ins6)
    )

)
