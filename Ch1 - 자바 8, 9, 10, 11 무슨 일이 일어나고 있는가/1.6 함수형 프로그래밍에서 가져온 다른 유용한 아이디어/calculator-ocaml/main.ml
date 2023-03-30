type exp =
  | X
  | INT of int
  | ADD of exp * exp
  | SUB of exp * exp
  | MUL of exp * exp
  | DIV of exp * exp
  | SIGMA of exp * exp * exp

let () = print_endline "Hello, World!"

let rec assignment : exp -> exp -> exp =
 fun num e ->
  match e with
  | X -> num
  | INT c -> INT c
  | ADD (e1, e2) -> ADD (assignment num e1, assignment num e2)
  | SUB (e1, e2) -> SUB (assignment num e1, assignment num e2)
  | MUL (e1, e2) -> MUL (assignment num e1, assignment num e2)
  | DIV (e1, e2) -> DIV (assignment num e1, assignment num e2)
  | SIGMA (p, q, e) -> sigma p q e

and sigma : exp -> exp -> exp -> exp =
 fun p q e ->
  match p, q with
  | INT a, INT b ->
    if a = b
    then assignment q e
    else if a < b
    then ADD (assignment p e, sigma (INT (a + 1)) q e)
    else ADD (assignment q e, sigma (INT (b + 1)) p e)
  | _ -> sigma (INT (calculator p)) (INT (calculator q)) e

and calculator : exp -> int =
 fun exp ->
  match exp with
  | INT num -> num
  | ADD (e1, e2) -> calculator e1 + calculator e2
  | SUB (e1, e2) -> calculator e1 - calculator e2
  | MUL (e1, e2) -> calculator e1 * calculator e2
  | DIV (e1, e2) -> calculator e1 / calculator e2
  | SIGMA (p, q, e) -> calculator (sigma p q e)
  | X -> 0
;;

(* 안 쓰이니까 그냥 아무 값이나 넣음 *)

let t1 = SIGMA (INT 1, INT 10, SUB (MUL (X, X), INT 1))
let t2 = SIGMA (ADD (INT 0, INT 1), MUL (INT 2, INT 5), SUB (MUL (X, X), INT 1));;

calculator t1;;
calculator t2
