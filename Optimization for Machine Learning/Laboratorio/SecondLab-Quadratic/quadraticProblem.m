
# Quadratic problem

# Si può specificare anche un x0 da dove partire. Non lo faremo.
#Dove c'è [] vuol dire vuoto
#[x, obj] = qp ([], H, q, A, b, lb, ub, A_lb, A_in, A_ub)


#H


H = [12 4 -6;
     4 10 0;
     -6 0 4;];

q = [6 -5 4];

A = [2 4 -5];

b = [6];

Ain = [ 3 -2 4;
        2 -3 1];

Alb = [-inf; 1];

Aub = [8; inf];

blb = [0; -inf; -inf];

bub = [inf; inf; 2];

[x, obj] = qp ([], H, q, A, b, blb, bub, Alb, Ain, Aub)

