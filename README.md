# Sistema de Matrículas Universitárias

Este repositório contém os artefatos de análise para a informatização do sistema de matrículas de uma universidade.

## Conteúdo

- `diagrama_casos_uso.drawio` — Diagrama de Casos de Uso (UML), editável no [draw.io](https://app.diagrams.net/).

## Diagrama de Casos de Uso

O diagrama identifica os seguintes **atores**:

- **Aluno** — matricula-se em disciplinas, cancela matrículas e consulta sua situação.
- **Professor** — consulta os alunos matriculados em suas disciplinas.
- **Secretaria** — gerencia cursos, disciplinas, currículo e o período de matrículas.
- **Sistema de Cobrança** *(sistema externo)* — recebe notificações para cobrar o aluno.

E os seguintes **casos de uso** principais:

- Efetuar Login
- Gerar Currículo do Semestre
- Cadastrar Curso
- Cadastrar Disciplina
- Matricular em Disciplina Obrigatória
- Matricular em Disciplina Optativa
- Cancelar Matrícula
- Consultar Situação da Matrícula
- Consultar Alunos Matriculados
- Notificar Sistema de Cobrança *(`<<include>>` em ambos os casos de matrícula)*
- Encerrar Período de Matrículas
- Processar Situação das Disciplinas — Ativar/Cancelar *(`<<extend>>` de "Encerrar Período de Matrículas")*

> Abra o arquivo `diagrama_casos_uso.drawio` em [app.diagrams.net](https://app.diagrams.net/) (File → Open From → Device) para visualizar e editar o diagrama.

---

## Histórias de Usuário

### Ator: Aluno

**US01** — Como aluno, quero fazer login no sistema com minha senha, para acessar as funcionalidades de matrícula.

**US02** — Como aluno, quero me matricular em até 4 disciplinas obrigatórias (1ª opção), para cursar as disciplinas do meu currículo no semestre.

**US03** — Como aluno, quero me matricular em até 2 disciplinas optativas (alternativas), para complementar minha formação.

**US04** — Como aluno, quero cancelar uma matrícula feita anteriormente, durante o período de matrículas, para ajustar minha grade de disciplinas.

**US05** — Como aluno, quero consultar a situação das minhas matrículas, para saber em quais disciplinas estou inscrito no semestre.

**US06** — Como aluno, quero ser impedido de me matricular em uma disciplina que já atingiu o limite de 60 alunos inscritos, para respeitar o limite de vagas da turma.

**US07** — Como aluno, quero ser cobrado automaticamente pelas disciplinas em que me matriculei, para regularizar o pagamento referente ao semestre.

### Ator: Professor

**US08** — Como professor, quero fazer login no sistema com minha senha, para acessar as informações das minhas disciplinas.

**US09** — Como professor, quero consultar a lista de alunos matriculados em cada disciplina que leciono, para me preparar para o semestre letivo.

### Ator: Secretaria

**US10** — Como secretaria, quero fazer login no sistema com minha senha, para gerenciar as informações acadêmicas da universidade.

**US11** — Como secretaria, quero cadastrar os cursos oferecidos pela universidade (nome e número de créditos), para manter o catálogo de cursos atualizado.

**US12** — Como secretaria, quero cadastrar as disciplinas que compõem cada curso, para compor o currículo do semestre.

**US13** — Como secretaria, quero gerar o currículo de cada semestre, para disponibilizar aos alunos as disciplinas que serão oferecidas.

**US14** — Como secretaria, quero encerrar o período de matrículas em uma data definida, para dar início ao processamento da situação das disciplinas.

**US15** — Como secretaria, quero que o sistema cancele automaticamente as disciplinas com menos de 3 alunos inscritos ao final do período de matrículas, para evitar a abertura de turmas inviáveis.

**US16** — Como secretaria, quero que o sistema mantenha ativas as disciplinas com pelo menos 3 alunos inscritos, para que elas ocorram normalmente no semestre seguinte.

**US17** — Como secretaria, quero que o sistema encerre automaticamente as inscrições de uma disciplina ao atingir 60 alunos matriculados, para respeitar o limite máximo de vagas por turma.

### Ator: Sistema de Cobrança (sistema externo)

**US18** — Como sistema de cobrança, quero ser notificado sempre que um aluno se matricular em disciplinas, para gerar a cobrança referente às disciplinas daquele semestre.

---

## Regras de Negócio (RN)

| Código | Regra |
|--------|-------|
| RN01 | Um aluno pode se matricular em no máximo 4 disciplinas obrigatórias e 2 disciplinas optativas por semestre. |
| RN02 | Uma disciplina só permanece ativa se tiver no mínimo 3 alunos inscritos ao final do período de matrículas; caso contrário, é cancelada. |
| RN03 | O número máximo de alunos matriculados por disciplina é 60; ao atingir esse limite, as inscrições na disciplina são encerradas automaticamente. |
| RN04 | Toda matrícula realizada deve notificar o sistema de cobrança, para que o aluno seja cobrado pelas disciplinas do semestre. |
| RN05 | Todos os usuários (alunos, professores e secretaria) autenticam-se no sistema por meio de login e senha. |