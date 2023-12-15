
clear;


H = 2 * [3 6 4;
         0 5 2;
         0 0 3];

q = [-1 10 -3];

A = [ 11 5 4];

b = [2];

A_in = [8 -6 4;
       6 7 9];

A_lb = [-inf; 1];

A_ub = [7; inf];

lb = [-inf; -2; -inf];

ub = [inf; 7; inf];

[x, obj] = qp ([], H, q, A, b, lb, ub, A_lb, A_in, A_ub)

#Open file in write
fid = fopen("output.txt", "w");

#Write the result
fprintf(fid, "Found solution x = %f %f %f with objective %f\n", x, obj);

fclose(fid);
