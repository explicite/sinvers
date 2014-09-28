setlocal
cd %3
set PP2D_DIR=%1
set FORGE2_IO=BIG_ENDIAN
set lang=eng
set WORK_DIR=%3
%PP2D_DIR%\bin\xf2_p1.exe %2
echo CLOSE COMPUTATION
cd ..
rd /S /Q %3