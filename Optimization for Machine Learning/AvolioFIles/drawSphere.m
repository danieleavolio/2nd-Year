## Copyright (C) 2022 Matte
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
## @deftypefn {} {@var{retval} =} drawSphere (@var{input1}, @var{input2})
##
## @seealso{}
## @end deftypefn

## Author: Matte <Matte@LAPTOP-GF9QKTOF>
## Created: 2022-12-11

function [] = drawSphere (x0, R, color)
  th = 0:pi/50:2*pi;

  xUnit = R * cos(th) + x0(1);

  yUnit = R * sin(th) + x0(2);

  plot(xUnit,yUnit,color);

  axis equal;

  legend('set A', 'set B', 'S(x0,R)','Location','northwestoutside');


endfunction
