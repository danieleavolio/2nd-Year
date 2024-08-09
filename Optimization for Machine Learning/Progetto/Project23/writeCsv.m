function [] = writeCsv(headers, data, filename)
    fid = fopen(filename, 'w');
    fprintf(fid, headers);
    fclose(fid);
    dlmwrite(filename, data, '-append');
end

