(defclass PAPEL             (is-a USER) (slot Timestamp) (slot Pregao))
(defclass TECHNICALANALISYS (is-a USER) (slot Timestamp) (slot Pregao))
(defclass COTACOES          (is-a USER) (slot Timestamp) (slot Pregao))

(defrule VerificarSeTodosDadosChegaram 
       ?Novo <- (DevoAnalisar ?t)
=> 
    (bind ?ContagemPapeis 0)
    (bind ?ContagemAnalisesTecnicas 0)
    (bind ?ContagemCotacoes 0)

    (do-for-all-instances ((?ins PAPEL)) (= 1 1)
         (bind ?ContagemPapeis (+ ?ContagemPapeis 1))
    )
    (do-for-all-instances ((?ins TECHNICALANALISYS)) (= ?ins:Pregao ?t )
         (bind ?ContagemAnalisesTecnicas (+ ?ContagemAnalisesTecnicas 1))         
    )
    (do-for-all-instances ((?ins COTACOES)) (= ?ins:Pregao ?t )
         (bind ?ContagemCotacoes (+ ?ContagemCotacoes 1))
    )
    (printout t "Papeis:" ?ContagemPapeis)
    (if (and (= ?ContagemPapeis ?ContagemAnalisesTecnicas) (= ?ContagemPapeis ?ContagemCotacoes)) then
        (LogCLIPS "Okay")
        (printout t "Okay" crlf) 
     else
        (LogCLIPS "NOK")
        (printout t "NOK" crlf)   
        (retract ?Novo)
        (assert (DevoAnalisar ?t))
    )
)

