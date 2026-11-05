Sistema de Gestão de Funcionários

Um sistema de secretária desenvolvido em Java com interface gráfica feita com Java Swing e base de dados relacional local feita com SQLite. Este projeto implementa as quatro operações fundamentais de um sistema: Create,Read,Update e Delete.

Suas funcionalidades

O sistema permite a gestão completa do quadro de funcionários através de uma interface intuitiva
Cadastrar: Inserção de novos funcionários com os seus respetivos dados (Nome, Cargo, Carga Horária, Salário, Vale Alimentação, Data de Admissão).
Consultar/Listar:Visualização de todos os funcionários registados numa tabela interativa com o "JTable".
Editar:Preenchimento automático do formulário ao clicar num funcionário na tabela, permitindo a atualização rápida dos seus dados.
Excluir:Remoção de registos da base de dados através da seleção na tabela, com aviso prévio de confirmação.

Tecnologias Utilizadas

Java(jdk 8): Linguagem principal do projeto.
Java Swing & AWT:Bibliotecas nativas do Java utilizadas para a construção da Interface Gráfica de Utilizador (GUI).
SQLite:Sistema de gestão de base de dados relacional leve e sem necessidade de servidor.
JDBC (Java Database Connectivity):API do Java utilizada para a comunicação com a base de dados SQLite.

A Arquitetura e Estrutura do Projeto

O código-fonte está organizado na pasta src/ e segue uma separação lógica de responsabilidades
 
FuncionarioDAO.java (A Persistência / DAO simplificado)
Classe utilitária que concentra toda a lógica de comunicação com o SQLite. Contém os comandos SQL INSERT, SELECT, UPDATE,DELETE, e a lógica de criação automática do ficheiro da base de dados e da tabela.

App.java (A Visualização e Controlo)
Classe principal que contém o método main(). É responsável por desenhar o ecrã (formulários, botões, tabela), capturar os eventos de clique do utilizador e invocar os métodos da classe FuncionarioDAO para processar as informações.

Esquema da Base de Dados

Ao executar o projeto pela primeira vez, a tabela funcionarios é criada automaticamente com a seguinte estrutura SQL

CREATE TABLE IF NOT EXISTS funcionarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    cargo TEXT,
    cargaHorariaSemanal INTEGER,
    salarioBase REAL,
    valeAlimentacao REAL,
    dataAdmissao DATE
);