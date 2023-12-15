## Copyright (C) 2023 danie
##
## This program is free software: you can redistribute it and/or modify
## it under the terms of the GNU General Public License as published by
## the Free Software Foundation, either version 3 of the License, or
## (at your option) any later version.
##
## This program is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
## GNU General Public License for more details.
##
## You should have received a copy of the GNU General Public License
## along with this program.  If not, see <https://www.gnu.org/licenses/>.

## -*- texinfo -*-
## @deftypefn {} {@var{retval} =} drawDataset (@var{input1}, @var{input2})
##
## @seealso{}
## @end deftypefn

## Author: danie <danie@LOVAION>
## Created: 2023-12-07

function retval = drawDataset (X, numPoints, y)

Plus = [];
Minus = [];

for i = 1: numPoints
    if y(i) == 1
        Plus = [Plus; X(i, :)];
    else
        Minus = [Minus; X(i, :)];
    endif
end

#Sto il minimo di tutte le righe per la prima colonna
xMin = min(X(:, 1));
#Sto il massimo di tutte le righe per la prima colonna
xMax = max(X(:, 1));

#Lo faccio anche per la Y
yMin = min(X(:, 2));
yMax = max(X(:, 2));

axis([xMin, xMax, yMin, yMax]);

hold on;
scatter(Plus(:, 1), Plus(:, 2), 'r', 'o', 'filled');

hold on;
scatter(Minus(:, 1), Minus(:, 2), 'b', 'o', 'filled');


endfunction
