

(defrule ComposicaoIndividamentoAlto
    ?ComposicaoIndividamento <- (object (is-a COMPOSICAOINDIVIDAMENTO) 
                               (Balancete  ?BalanceteAtual)
                               (Valor      ?ValorInd&:(> ?ValorInd 80)))
=>
    (bind ?y (str-cat "Essa empresa tem um alto individamento de curto prazo" ?BalanceteAtual crlf))
    (printout t ?y)
    (bind ?t (str-cat "Valor=-1;Descricao=Essa empresa tem um alto individamento de curto prazo;Tipo=ComposicaoIndividamentoAnalisys;Balancete=" ?BalanceteAtual))
    (WriteToBlackboard "FundamentalSubAnalisys" ?t)
    (unmake-instance ?ComposicaoIndividamento  )
)


(defrule ComposicaoIndividamentoNormal
    ?ComposicaoIndividamento <- (object (is-a COMPOSICAOINDIVIDAMENTO) 
                               (Balancete  ?BalanceteAtual)
                               (Valor      ?ValorInd&:(< ?ValorInd 80)&:(> ?ValorInd 20)))
=>
    (bind ?y (str-cat "Essa empresa tem um individamento de curto prazo considerado normal" ?BalanceteAtual  crlf))
    (printout t ?y)
    (bind ?t (str-cat "Valor=0;Descricao=Essa empresa tem um alto individamento de curto prazo;Tipo=ComposicaoIndividamentoAnalisys;Balancete=" ?BalanceteAtual))
    (WriteToBlackboard "FundamentalSubAnalisys" ?t)
    (unmake-instance ?ComposicaoIndividamento  )
)

(defrule ComposicaoIndividamentoBaixo
    ?ComposicaoIndividamento <- (object (is-a COMPOSICAOINDIVIDAMENTO) 
                               (Balancete  ?BalanceteAtual)
                               (Valor      ?ValorInd&:(< ?ValorInd 20)))
=>
    (bind ?y (str-cat "Essa empresa tem um baixo individamento de curto prazo" ?BalanceteAtual  crlf))
    (printout t ?y)
    (bind ?t (str-cat "Valor=1;Descricao=Essa empresa tem um alto individamento de curto prazo;Tipo=ComposicaoIndividamentoAnalisys;Balancete=" ?BalanceteAtual))
    (WriteToBlackboard "FundamentalSubAnalisys" ?t)
    (unmake-instance ?ComposicaoIndividamento  )
)

(defrule GiroDoAtivoAlto
    ?GiroDoAtivo <- (object (is-a GIRODOATIVO) 
                               (Balancete  ?BalanceteAtual)
                               (Valor      ?ValorInd&:(> ?ValorInd 80)))
=>
    (bind ?y (str-cat "Essa empresa tem um alto giro do ativo"  ?BalanceteAtual crlf))
    (printout t ?y)
    (bind ?t (str-cat "Valor=-1;Descricao=Essa empresa tem um alto giro do ativo;Tipo=GiroDoAtivoAnalisys;Balancete=" ?BalanceteAtual))
    (WriteToBlackboard "FundamentalSubAnalisys" ?t)
    (unmake-instance ?GiroDoAtivo )
)


(defrule GiroDoAtivoNormal
    ?GiroDoAtivo <- (object (is-a GIRODOATIVO) 
                               (Balancete  ?BalanceteAtual)
                               (Valor      ?ValorInd&:(< ?ValorInd 80)&:(> ?ValorInd 20)))
=>
    
    (bind ?y (str-cat "Essa empresa tem um giro do ativo normal" ?BalanceteAtual))
    (printout t ?y)
    (bind ?t (str-cat "Valor=0;Descricao=Essa empresa tem um giro do ativo normal;Tipo=GiroDoAtivoAnalisys;Balancete=" ?BalanceteAtual))
    (WriteToBlackboard "FundamentalSubAnalisys" ?t)
    (unmake-instance ?GiroDoAtivo )
)

(defrule GiroDoAtivoBaixo
    ?GiroDoAtivo <- (object (is-a GIRODOATIVO) 
                               (Balancete  ?BalanceteAtual)
                               (Valor      ?ValorInd&:(< ?ValorInd 20)))
=>
    (bind ?y (str-cat "Essa empresa tem um baixo giro do ativo" ?BalanceteAtual crlf))
    (printout t ?y)
    (bind ?t (str-cat "Valor=1;Descricao=Essa empresa tem um baixo giro do ativo;Tipo=GiroDoAtivoAnalisys;Balancete=" ?BalanceteAtual))
    (WriteToBlackboard "FundamentalSubAnalisys" ?t)
    (unmake-instance ?GiroDoAtivo )
)

(defrule ImobilizacaoPatrimonioLiquidoAlto
    ?ImobilizacaoPatrimonioLiquido <- (object (is-a IMOBILIZACAOPATRIMONIOLIQUIDO) 
                               (Balancete  ?BalanceteAtual)
                               (Valor      ?ValorInd&:(> ?ValorInd 80)))
=>
    (bind ?y (str-cat "Essa empresa tem uma alta imobilizacao do patrimonio liquido" ?BalanceteAtual crlf))
    (printout t ?y)
    (bind ?t (str-cat "Valor=-1;Descricao=Essa empresa tem uma alta imobilizacao do patrimonio liquido;Tipo=ImobilizacaoPatrimonioLiquidoAnalisys;Balancete=" ?BalanceteAtual))
    (WriteToBlackboard "FundamentalSubAnalisys" ?t)
    (unmake-instance ?ImobilizacaoPatrimonioLiquido )
)

(defrule ImobilizacaoPatrimonioLiquidoNormal
    ?ImobilizacaoPatrimonioLiquido <- (object (is-a IMOBILIZACAOPATRIMONIOLIQUIDO) 
                               (Balancete  ?BalanceteAtual)
                               (Valor      ?ValorInd&:(< ?ValorInd 80)&:(> ?ValorInd 20)))
=>
    (bind ?y (str-cat "Essa empresa tem uma imobilizacao do patrimonio liquido normal" ?BalanceteAtual crlf))
    (printout t ?y)
    (bind ?t (str-cat "Valor=0;Descricao=Essa empresa tem uma imobilizacao do patrimonio liquido normal;Tipo=ImobilizacaoPatrimonioLiquidoAnalisys;Balancete=" ?BalanceteAtual))
    (WriteToBlackboard "FundamentalSubAnalisys" ?t)
    (unmake-instance ?ImobilizacaoPatrimonioLiquido )
)

(defrule ImobilizacaoPatrimonioLiquidoBaixo
    ?ImobilizacaoPatrimonioLiquido <- (object (is-a IMOBILIZACAOPATRIMONIOLIQUIDO) 
                               (Balancete  ?BalanceteAtual)
                               (Valor      ?ValorInd&:(< ?ValorInd 20)))
=>
    (bind ?y (str-cat "Essa empresa tem uma baixa imobilizacao do patrimonio liquido" ?BalanceteAtual crlf))
    (printout t ?y)
    (bind ?t (str-cat "Valor=1;Descricao=Essa empresa tem uma baixa imobilizacao do patrimonio liquido;Tipo=ImobilizacaoPatrimonioLiquidoAnalisys;Balancete=" ?BalanceteAtual))
    (WriteToBlackboard "FundamentalSubAnalisys" ?t)
    (unmake-instance ?ImobilizacaoPatrimonioLiquido )
)


(defrule LiquidezCorrenteAlto
    ?LiquidezCorrente <- (object (is-a LIQUIDEZCORRENTE) 
                               (Balancete  ?BalanceteAtual)
                               (Valor      ?ValorInd&:(> ?ValorInd 80)))
=>
    (bind ?y (str-cat "Essa empresa tem uma liquidez corrente alta" ?BalanceteAtual crlf))
    (printout t ?y)
    (bind ?t (str-cat "Valor=-1;Descricao=Essa empresa tem uma liquidez corrente alta;Tipo=LiquidezCorrenteAnalisys;Balancete=" ?BalanceteAtual))
    (WriteToBlackboard "FundamentalSubAnalisys" ?t)
    (unmake-instance ?LiquidezCorrente )
)

(defrule LiquidezCorrenteNormal
    ?LiquidezCorrente <- (object (is-a LIQUIDEZCORRENTE) 
                               (Balancete  ?BalanceteAtual)
                               (Valor      ?ValorInd&:(< ?ValorInd 80)&:(> ?ValorInd 20)))
=>
    (bind ?y (str-cat "Essa empresa tem uma liquidez corrente normal" ?BalanceteAtual crlf))
    (printout t ?y)
    (bind ?t (str-cat "Valor=0;Descricao=Essa empresa tem uma liquidez corrente normal;Tipo=LiquidezCorrenteAnalisys;Balancete=" ?BalanceteAtual))
    (WriteToBlackboard "FundamentalSubAnalisys" ?t)
    (unmake-instance ?LiquidezCorrente )
)

(defrule LiquidezCorrenteBaixo
    ?LiquidezCorrente <- (object (is-a LIQUIDEZCORRENTE) 
                               (Balancete  ?BalanceteAtual)
                               (Valor      ?ValorInd&:(< ?ValorInd 20)))
=>
    (bind ?y (str-cat "Essa empresa tem uma liquidez corrente baixa" ?BalanceteAtual crlf))
    (printout t ?y)
    (bind ?t (str-cat "Valor=1;Descricao=Essa Essa empresa tem uma liquidez corrente baixa;Tipo=LiquidezCorrenteAnalisys;Balancete=" ?BalanceteAtual))
    (WriteToBlackboard "FundamentalSubAnalisys" ?t)
    (unmake-instance ?LiquidezCorrente )
)


(defrule LiquidezGeralAlto
    ?LiquidezGeral <- (object (is-a LIQUIDEZGERAL) 
                               (Balancete  ?BalanceteAtual)
                               (Valor      ?ValorInd&:(> ?ValorInd 80)))
=>
    (bind ?y (str-cat "Essa empresa tem uma liquidez geral alta" ?BalanceteAtual crlf))
    (printout t ?y)
    (bind ?t (str-cat "Valor=-1;Descricao=Essa empresa tem uma liquidez geral alta;Tipo=LiquidezGeralAnalisys;Balancete=" ?BalanceteAtual))
    (WriteToBlackboard "FundamentalSubAnalisys" ?t)
    (unmake-instance ?LiquidezGeral )
)

(defrule LiquidezGeralNormal
    ?LiquidezGeral <- (object (is-a LIQUIDEZGERAL) 
                               (Balancete  ?BalanceteAtual)
                               (Valor      ?ValorInd&:(< ?ValorInd 80)&:(> ?ValorInd 20)))
=>
    (bind ?y (str-cat "Essa empresa tem uma liquidez geral normal" ?BalanceteAtual crlf))
    (printout t ?y)
    (bind ?t (str-cat "Valor=0;Descricao=Essa empresa tem uma liquidez geral normal;Tipo=LiquidezGeralAnalisys;Balancete=" ?BalanceteAtual))
    (WriteToBlackboard "FundamentalSubAnalisys" ?t)
    (unmake-instance ?LiquidezGeral )
)

(defrule LiquidezGeralBaixo
    ?LiquidezGeral <- (object (is-a LIQUIDEZGERAL) 
                               (Balancete  ?BalanceteAtual)
                               (Valor      ?ValorInd&:(< ?ValorInd 20)))
=>
    (bind ?y (str-cat "Essa empresa tem uma liquidez geral baixa" ?BalanceteAtual crlf))
    (printout t ?y)
    (bind ?t (str-cat "Valor=1;Descricao=Essa empresa tem uma liquidez geral baixa;Tipo=LiquidezGeralAnalisys;Balancete=" ?BalanceteAtual))
    (WriteToBlackboard "FundamentalSubAnalisys" ?t)
    (unmake-instance ?LiquidezGeral )
)


(defrule RetornoSobreVendasAlto
    ?RetornoSobreVendas <- (object (is-a RETORNOSOBREVENDAS) 
                               (Balancete  ?BalanceteAtual)
                               (Valor      ?ValorInd&:(> ?ValorInd 80)))
=>
    (bind ?y (str-cat "Essa empresa tem um alto retorno sobre vendas" ?BalanceteAtual crlf))
    (printout t ?y)
    (bind ?t (str-cat "Valor=-1;Descricao=Essa empresa tem um alto retorno sobre vendas;Tipo=RetornoSobreVendasAnalisys;Balancete=" ?BalanceteAtual))
    (WriteToBlackboard "FundamentalSubAnalisys" ?t)
    (unmake-instance ?RetornoSobreVendas)
)

(defrule RetornoSobreVendasNormal
    ?RetornoSobreVendas <- (object (is-a RETORNOSOBREVENDAS) 
                               (Balancete  ?BalanceteAtual)
                               (Valor      ?ValorInd&:(< ?ValorInd 80)&:(> ?ValorInd 20)))
=>
    (bind ?y (str-cat "Essa empresa tem um alto retorno sobre vendas normal" ?BalanceteAtual crlf))
    (printout t ?y)
    (bind ?t (str-cat "Valor=0;Descricao=Essa empresa tem um alto retorno sobre vendas normal;Tipo=RetornoSobreVendasAnalisys;Balancete=" ?BalanceteAtual))
    (WriteToBlackboard "FundamentalSubAnalisys" ?t)
    (unmake-instance ?RetornoSobreVendas)
)


(defrule RetornoSobreVendasBaixo
    ?RetornoSobreVendas <- (object (is-a RETORNOSOBREVENDAS) 
                               (Balancete  ?BalanceteAtual)
                               (Valor      ?ValorInd&:(< ?ValorInd 20)))
=>
    (bind ?y (str-cat "Essa empresa tem um baixo retorno sobre vendas normal" ?BalanceteAtual crlf))
    (printout t ?y)
    (bind ?t (str-cat "Valor=1;Descricao=Essa empresa tem um baixo retorno sobre vendas baixo;Tipo=RetornoSobreVendasAnalisys;Balancete=" ?BalanceteAtual))
    (WriteToBlackboard "FundamentalSubAnalisys" ?t)
    (unmake-instance ?RetornoSobreVendas)
)

(defrule ParecerFinal 
    ?AnaliseComposicao      <- (object (is-a FUNDAMENTALSUBANALISYS) (Tipo "ComposicaoIndividamentoAnalisys") (Balancete ?t1) (Valor ?v1))
    ?AnaliseGiroDoAtivo     <- (object (is-a FUNDAMENTALSUBANALISYS)             (Tipo "GiroDoAtivoAnalisys") (Balancete ?t1) (Valor ?v2))
    ?AnaliseLiquidezCorrente<- (object (is-a FUNDAMENTALSUBANALISYS)        (Tipo "LiquidezCorrenteAnalisys") (Balancete ?t1) (Valor ?v3))
    ?AnaliseLiquidezGeral   <- (object (is-a FUNDAMENTALSUBANALISYS)           (Tipo "LiquidezGeralAnalisys") (Balancete ?t1) (Valor ?v4))
    ?AnaliseRetorno         <- (object (is-a FUNDAMENTALSUBANALISYS)      (Tipo "RetornoSobreVendasAnalisys") (Balancete ?t1) (Valor ?v5))
=>
    (bind ?y (str-cat "Gerei Analise Fundamentalista" ?t1 crlf))
    (printout t ?y)
    (bind ?t (str-cat "Valor=0;Descricao=A analise do Oscilador Estocastico, nao indica nada de novo;Tipo=StochasticAnalisys;Balancete=" ?t1)) 
    (WriteToBlackboard "FundamentalAnalisys" ?t)
)

