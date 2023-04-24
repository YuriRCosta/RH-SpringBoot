<h1>Projeto de Cadastro de Pessoas e Currículos</h1>
<p>Este é um projeto desenvolvido em Java utilizando Spring Boot, Spring Security, Thymeleaf, Jasper Reports para cadastrar pessoas, seus currículos e informações de contato, podendo buscar por essas pessoas e imprimir relatorios e os curriculos. Além disso, permite que os usuários com diferentes funções tenham diferentes permissões, definidas por suas "Roles", alem das senhas estarem criptografadas.</p>

<h2>Requisitos</h2>
Para rodar este projeto, é necessário ter o Java 8 ou superior e o Maven instalados em sua máquina. É recomendável também ter o PostgreSQL ou outro banco de dados relacional instalado e configurado.

<h2>Instalação</h2>
Clone este repositório em sua máquina:</br></br>
https://github.com/YuriRCosta/RH-SpringBoot.git</br></br>
Crie um banco de dados PostgreSQL chamado "cadastro" ou outro nome desejado.</br></br>

Edite o arquivo application.properties localizado em src/main/resources/ e configure o acesso ao banco de dados:

spring.datasource.url=jdbc:postgresql://localhost:5432/cadastro</br>
spring.datasource.username=seu-usuario</br>
spring.datasource.password=sua-senha

Acesse a aplicação em http://localhost:8080/ em seu navegador.

<h2>Funcionalidades</h2>
- Cadastro de pessoas, com nome, sobrenome, profissao, curriculo, genero, CEP;</br>
- Cadastro de telefones para pessoas, podendo imprimir relatorios com as informacoes;</br>
- Possibilidade de buscar e imprimir os currículos cadastrados;</br>
- Geração de relatórios com as informações de todas as pessoas cadastradas;</br>
- Controle de acesso baseado em Roles, permitindo que usuários com diferentes funções tenham diferentes permissões na aplicação;</br>
- Senhas dos usuarios criptografadas;
<h2>Tecnologias utilizadas</h2>

- Spring Boot</br>
- Spring Security</br>
- Thymeleaf</br>
- Jasper Reports</br>
- jQuery</br>
<h2>Desenvolvedor</h2>

Nome: Yuri Ramos Costa</br>
Email: yuriramoscosta@hotmail.com</br>
GitHub: https://github.com/YuriRCosta</br>
LinkedIn: https://www.linkedin.com/in/yuriramoscosta/
