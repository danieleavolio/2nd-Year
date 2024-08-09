function f_score = f_score(sensitivity, precision)
    f_score = 2 * sensitivity * precision / (sensitivity + precision);
end

