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
## @deftypefn {} {@var{retval} =} drawHyperplane (@var{input1}, @var{input2})
##
## @seealso{}
## @end deftypefn

## Author: danie <danie@LOVAION>
## Created: 2023-12-07

function retval = drawHyperplane (X, v, gamma)

  x1 = min(X(:,1)):0.1:max(X(:,1))


  x2 = (gamma - v(1) * x1) / v(2);

  hold on;

  plot(x1, x2, 'k');

  %Supporting hyperplanes
  gammaPlus = gamma + 1;
  gammaMinus = gamma - 1;

  x2Plus = (gammaPlus - v(1) * x1) / v(2);
  x2Minus = (gammaMinus - v(1) * x1) / v(2);

  plot(x1, x2Plus, 'r');

  plot(x1, x2Minus, 'b');
endfunction
