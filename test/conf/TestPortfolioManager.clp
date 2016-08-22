(defclass PAPEL   (is-a USER) (slot Timestamp))
(defclass COTACAO (is-a USER) 
    (slot Timestamp)
    (slot Pregao)
    (slot Papel) 
    (slot Fechamento) 
)

(defrule AdicionarPapel
    ?NovoPapel <- (object (is-a PAPEL) 
                               (Timestamp  ?Timest))
=>
    (printout t "Adicionando Papel" ?Timest crlf)
    (bind ?t (str-cat ?Timest))
    (AddPapel ?t)
    (unmake-instance ?NovoPapel)
)

(defrule AdicionarQuote
    ?NovaCotacao <- (object (is-a COTACAO) 
                               (Papel  ?Papel2)
                               (Pregao  ?Preg)
                               (Fechamento  ?Fech))
=>
    (printout t "Adicionando Quote" crlf)
    (bind ?t1 (str-cat ?Papel2))
    (bind ?t2 (str-cat ?Preg))
    (bind ?t3 (str-cat ?Fech))
    (AddQuote ?t1 ?t2 ?t3)
    (unmake-instance ?NovaCotacao)
)

