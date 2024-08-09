% Funzione kernel gaussiano
function K = K(x, y, sigm)
    % Apply Norma l2
    K = exp((-norm(x - y, 2)^2) / (2 * sigm^2));
    % printf("K: %f\n", K);
endfunction
