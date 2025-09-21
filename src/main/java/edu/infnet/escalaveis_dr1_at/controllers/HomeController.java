package edu.infnet.escalaveis_dr1_at.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return HOME_HTML;
    }

    @GetMapping("/api/")
    public String apiHome() {
        return "Pong";
    }

    @GetMapping("/ping")
    public String ping() {
        return "Pong";
    }

    @GetMapping("/auth-ping")
    public String AuthPing() {
        return "Authenticated Pong";
    }

    private static final String HOME_HTML = """
    <style>
        body {
            background-color: #f0f0f0;
            margin: 0;
            font-family: Helvetica;
        }
        .box {
            background-color: #0000ff30; 
            padding: 20px; 
            margin:20px ; 
            border-radius: 10px; 
        }
        hr{
            border-bottom: 1px solid #00000010;
            width: 90%;
            margin: 20px auto;
        }
        .code{
            font-family: 'Courier New', monospace;
            background-color: #606060;
            color: #ffffff;
            padding: 2px 5px;
            border-radius: 2px;
        }
    </style>
    <div class="box" >
    <h1 style="text-align: center">API de Gestão Acadêmica</h1>
    <hr/>

    <div
        style="display: flex; justify-content: center; align-items: center; margin-top: 20px; margin-bottom: 20px; gap: 20px;">
        <div style="text-align: center; background-color: #ffffff60; padding: 20px; border-radius: 5px;">
            <img src="https://static1.smartbear.co/swagger/media/assets/images/swagger_logo.svg"
                style="height: 40px; margin: 5px;">
            <p>
                Endpoints disponiveis <b><a href="/swagger-ui/index.html" target="_blank">AQUI</a></b>
            </p>
        </div>

        <div style="text-align: center; background-color: #ffffff60; padding: 20px; border-radius: 5px;">
            <img src="https://images.seeklogo.com/logo-png/44/2/openapi-logo-png_seeklogo-442295.png"
                style="height: 40px; margin: 5px;">
            <p>
                Api specs disponivel <b><a href="/v3/api-docs" target="_blank">AQUI</a></b>
            <p>
        </div>

        <div style="text-align: center; background-color: #ffffff60; padding: 20px; border-radius: 5px;">
            <img src="https://github.githubassets.com/assets/GitHub-Mark-ea2971cee799.png"
                style="height: 40px; margin: 5px;">
            <p>
                Repositorio Github disponivel <b><a href="https://github.com/jvcmtr/escalaveis_DR1_AT">AQUI</a></b>
            </p>
        </div>
    </div>

    <hr/>

    <div>
        <h2 style="margin-left: 40px">Como usar:</h2>
        
        <div class="box">
            <h3>1. Registrar-se como Professor:</h3>
            <p>
                Acesse <span class="code"> POST /api/auth/register</span> Através do 
                <b><a href="/swagger-ui/index.html#/auth-controller/register" target="_blank">swagger</a></b> 
                e registre-se. 
            <p>
            <b> Exemplo de payload JSON: </b>
            <div class="code" style="display: inline-block; padding: 20px">
                { <br/>
                 <div style="margin-left: 20px">
                    "nome" : "<b>Seu Nome</b>", <br/>
                    "email": "<b>seu.email@email.com</b>", <br/>
                    "senha": "<b>suaSenha123</b>" <br/>
                 </div>
                }
            </div>

        </div>
        <div class="box">
            <h3>2. Fazer Login:</h3>
            <p>
                Faça login clicando <b><a href="/login" target="_blank"> AQUI</a></b> 
            <p>
        </div>

        <div class="box">
            <h3>3. Operar o sistema:</h3>
            <ul>
                <li>
                    Cadastre disciplinas e alunos atravéz dos respectivos controllers no
                    <b><a href="/swagger-ui/index.html" target="_blank">swagger</a></b>
                </li>
                <li>
                    Filtre a listagem de alunos por disciplina e aprovação usado os <b>parametros de query</b> em
                    descritos no <b><a href="/swagger-ui/index.html#/aluno-controller/getAllAlunos" target="_blank">swagger</a></b>.
               </li> 
               <li>
                    <b>Matricule alunos</b> em disciplinas atravez do 
                    <b><a href="/swagger-ui/index.html#/aluno-controller/martricularAluno" target="_blank">deste endpoint</a></b>
               </li> 
                <li>
                    <b>Avalie os alunos</b> em suas disciplinas clicando 
                    <b><a href="/swagger-ui/index.html#/aluno-controller/avaliarAluno" target="_blank">aqui</a></b>
               </li> 
            <ul>
        </div>
    </div>

    <hr/>

    <p style="color: #00000070; text-align: center;">
        João Cícero 
        - Rio de Janeiro, Setembro de 2025
        - Assessment da disciplina de Desenvolvimento de Serviços com Spring Boot
        - Instituto Infnet 
    </p>
    </div>
""";
}
