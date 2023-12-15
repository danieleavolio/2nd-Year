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
## @deftypefn {} {@var{retval} =} sum_and_diff (@var{input1}, @var{input2})
##
## @seealso{}
## @end deftypefn

## Author: danie <danie@LOVAION>
## Created: 2023-11-23

function retval = sum_and_diff (input1, input2)

sum = input1 + input2;

diff = input1 - input2;

retval = [sum diff];

endfunction
