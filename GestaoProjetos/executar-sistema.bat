@echo off
chcp 65001 > nul
title Sistema Gestao Projetos
echo Iniciando Sistema Completo...
echo.

REM Compilar para a pasta bin
if not exist "bin" mkdir bin
javac -d bin -cp src src/a/ApiServer.java src/a/SistemaGestaoProjetos.java

if errorlevel 1 (
    echo ERRO: Falha na compilacao!
    echo Verifique os erros acima.
    pause
    exit /b 1
)

REM Executar diretamente da pasta bin
echo Iniciando servidor API...
cd bin
start "API Server" java a.ApiServer
cd ..

echo Aguardando inicializacao...
timeout /t 6 > nul

echo Abrindo interface...
start http://localhost:8080

echo.
echo ✓ Sistema iniciado!
echo ✓ API: http://localhost:8080/api
echo ✓ Frontend: http://localhost:8080
echo.
echo Pressione qualquer tecla para fechar...
pause > nul