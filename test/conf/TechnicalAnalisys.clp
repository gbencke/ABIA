(defrule AvaliarBollingerBandsSuperComprada
    ?BollingerNova <- (object (is-a BOLLINGERBANDS) 
                               (lowerBand  ?ValorlowerBand)
                               (upperBand  ?ValorupperBand)
                               (Fechamento ?ValorFechamento&:(<= ?ValorupperBand ?ValorFechamento))
                               (Pregao     ?PregaoAtual)
                               (middleBand ?ValormiddleBand)
                               (Papel      ?ValorPapel&:(> ?ValorPapel 0 )))

=>
    (printout t "A Acao do papel esta super-comprada de acordo com Bollinger" crlf);
    (bind ?t (str-cat "Valor=-1;Descricao=Esse papel esta super-comprado,pois a banda superior de Bollinger eh maior que o fechamento;Tipo=BollingerBandsAnalisys;Papel=" ?ValorPapel 
              ";Pregao=" ?PregaoAtual)) 
    (WriteToBlackboard "TechnicalSubAnalisys" ?t)
    (unmake-instance ?BollingerNova )
)

(defrule AvaliarBollingerBandsDesvalorizada
    ?BollingerNova <- (object (is-a BOLLINGERBANDS) 
                               (lowerBand  ?ValorlowerBand)
                               (upperBand  ?ValorupperBand)
                               (Fechamento ?ValorFechamento&:(>= ?ValorlowerBand ?ValorFechamento))
                               (Pregao     ?PregaoAtual)
                               (middleBand ?ValormiddleBand)
                               (Papel      ?ValorPapel&:(> ?ValorPapel 0 )))
=>
    (printout t "A Acao do papel esta desvalorizada de acordo com Bollinger" crlf);
    (bind ?t (str-cat "Valor=1;Descricao=Esse papel esta desvalorizada,pois a banda inferior de Bollinger eh menor que o fechamento;Tipo=BollingerBandsAnalisys;Papel=" ?ValorPapel 
              ";Pregao=" ?PregaoAtual)) 
    (WriteToBlackboard "TechnicalSubAnalisys" ?t)
    (unmake-instance ?BollingerNova )
)

(defrule AvaliarBollingerBandsNormal
    ?BollingerNova <- (object (is-a BOLLINGERBANDS) 
                               (lowerBand  ?ValorlowerBand)
                               (upperBand  ?ValorupperBand)
                               (Papel      ?ValorPapel&:(> ?ValorPapel 0 ))
                               (Pregao     ?PregaoAtual)
                               (Fechamento ?ValorFechamento&:(> ?ValorupperBand ?ValorFechamento)&:(< ?ValorlowerBand ?ValorFechamento)))
=>
    (printout t "A analise de Bollinger, nao indica nada de novo" crlf)
    (bind ?t (str-cat "Valor=0;Descricao=A analise de Bollinger, nao indica nada de novo;Tipo=BollingerBandsAnalisys;Papel=" ?ValorPapel 
              ";Pregao=" ?PregaoAtual)) 
    (WriteToBlackboard "TechnicalSubAnalisys" ?t)
    (unmake-instance ?BollingerNova )
)




(defrule AvaliarRSISuperComprada
    ?RSINova <- (object (is-a RSI) 
                               (Valor      ?ValorAnalisado&:(>= ?ValorAnalisado 80))
                               (Pregao     ?PregaoAtual)
                               (Papel      ?ValorPapel&:(> ?ValorPapel 0 )))

=>
    (printout t "A Acao do papel esta super-comprada de acordo com o RSI" crlf);
    (bind ?t (str-cat "Valor=-1;Descricao=Esse papel esta super-comprado,pois o valor do RSI eh superior a 80%;Tipo=RSIAnalisys;Papel=" ?ValorPapel 
              ";Pregao=" ?PregaoAtual)) 
    (WriteToBlackboard "TechnicalSubAnalisys" ?t)
    (unmake-instance ?RSINova )
)

(defrule AvaliarRSIDesvalorizada
    ?RSINova <- (object (is-a RSI) 
                               (Valor      ?ValorAnalisado&:(<= ?ValorAnalisado 20))
                               (Pregao     ?PregaoAtual)
                               (Papel      ?ValorPapel&:(> ?ValorPapel 0 )))
=>
    (printout t "A Acao do papel esta desvalorizada de acordo com RSI" crlf);
    (bind ?t (str-cat "Valor=1;Descricao=Esse papel esta desvalorizada,pois o valor do RSI eh inferior a 20% ;Tipo=RSIAnalisys;Papel=" ?ValorPapel 
              ";Pregao=" ?PregaoAtual)) 
    (WriteToBlackboard "TechnicalSubAnalisys" ?t)
    (unmake-instance ?RSINova )
)

(defrule AvaliarRSINormal
    ?RSINova <- (object (is-a RSI) 
                               (Valor      ?ValorAnalisado&:(> ?ValorAnalisado 20 )&:(< ?ValorAnalisado 80 ))
                               (Papel      ?ValorPapel&:(> ?ValorPapel 0 ))
                               (Pregao     ?PregaoAtual))
=>
    (printout t "A analise de RSI, nao indica nada de novo" crlf)
    (bind ?t (str-cat "Valor=0;Descricao=A analise de Bollinger, nao indica nada de novo;Tipo=RSIAnalisys;Papel=" ?ValorPapel 
              ";Pregao=" ?PregaoAtual)) 
    (WriteToBlackboard "TechnicalSubAnalisys" ?t)
    (unmake-instance ?RSINova )
)



(defrule AvaliarStochasticoSuperComprada
    ?StochasticoNovo <- (object (is-a STOCHASTICOSCILLATOR) 
                               (D ?D)
                               (K ?K&:(<= ?K ?D))
                               (Pregao     ?PregaoAtual)
                               (Papel      ?ValorPapel&:(> ?ValorPapel 0 )))

=>
    (printout t "O Valor de %D se tornou superior a %K, a acao esta super-valorizada" crlf);
    (bind ?t (str-cat "Valor=-1;Descricao=Esse papel esta super-comprado,pois o Valor de %D eh maior que %K;Tipo=StochasticAnalisys;Papel=" ?ValorPapel 
              ";Pregao=" ?PregaoAtual)) 
    (WriteToBlackboard "TechnicalSubAnalisys" ?t)
    (unmake-instance ?StochasticoNovo )
)

(defrule AvaliarStochasticoNormal
    ?StochasticoNovo <- (object (is-a STOCHASTICOSCILLATOR) 
                               (D ?D)
                               (K ?K&:(> ?K ?D))
                               (Papel      ?ValorPapel&:(> ?ValorPapel 0 ))
                               (Pregao     ?PregaoAtual))
=>
    (printout t "A analise do Oscilador Stocastico, nao indica nada de novo" crlf)
    (bind ?t (str-cat "Valor=0;Descricao=A analise do Oscilador Estocastico, nao indica nada de novo;Tipo=StochasticAnalisys;Papel=" ?ValorPapel 
              ";Pregao=" ?PregaoAtual)) 
    (WriteToBlackboard "TechnicalSubAnalisys" ?t)
    (unmake-instance ?StochasticoNovo )
)

(defrule ParecerFinal 
    ?AnaliseStocastico <- (object (is-a TECHNICALSUBANALISYS) (Tipo "StochasticAnalisys") (Valor ?ValStocastico) (Papel ?ValorPapel&:(> ?ValorPapel 0 )) (Pregao ?PregaoAtual) )
    ?AnaliseRSI        <- (object (is-a TECHNICALSUBANALISYS) (Tipo "RSIAnalisys") (Valor ?ValStocastico1) (Papel ?ValorPapel&:(> ?ValorPapel 0 )) (Pregao ?PregaoAtual) )
    ?AnaliseBollinger  <- (object (is-a TECHNICALSUBANALISYS) (Tipo "BollingerBandsAnalisys")(Valor ?ValStocastico2) (Papel ?ValorPapel&:(> ?ValorPapel 0 )) (Pregao ?PregaoAtual) )
=>
    (printout t "Gerei Analise Tecnica" crlf)
    (bind ?t (str-cat "Valor=0;Descricao=A analise do Oscilador Estocastico, nao indica nada de novo;Tipo=StochasticAnalisys;Papel=" ?ValorPapel 
              ";Pregao=" ?PregaoAtual)) 
    (WriteToBlackboard "TechnicalAnalisys" ?t)
)

