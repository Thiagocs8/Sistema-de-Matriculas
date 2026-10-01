# Sistema de Matrículas Universitárias

Este repositório contém os artefatos de análise para a informatização do sistema de matrículas de uma universidade.

## Alunos
- Caio Kfuri
- João Heleno 
- Thiago Costa Soares

## Conteúdo

- `docs/diagrama_casos_uso.drawio` — Diagrama de Casos de Uso (UML), editável no [draw.io](https://app.diagrams.net/).
- `docs/diagrama_classes.drawio.pdf` — Diagrama de Classes (UML).
- `src/main/java` — Projeto Java (Maven) com o protótipo do sistema: interface em linha de comando e persistência em arquivos.

## Projeto Estrutural (Diagrama de Classes)

O projeto Java (Maven, `pom.xml`) implementa a estrutura definida no diagrama de classes, organizada nos pacotes:

- `br.pucminas.matriculas.model` — `Usuario` (abstrata), `Aluno`, `Professor`, `Secretaria`, `Curso`, `Curriculo`, `Disciplina`, `PeriodoMatricula`, `Matricula`.
- `br.pucminas.matriculas.model.enums` — `StatusDisciplina`, `TipoMatricula`, `StatusMatricula`, `StatusPeriodo`.
- `br.pucminas.matriculas.external` — `SistemaCobranca` (interface para o sistema externo de cobrança) e `SistemaCobrancaArquivo` (simulação que registra cada notificação em arquivo).
- `br.pucminas.matriculas.service` — `SistemaMatriculas` (fachada: login, cadastros, período de matrículas, matrícula/cancelamento e notificação da cobrança) e `RegraNegocioException`.
- `br.pucminas.matriculas.persistence` — `RepositorioArquivos` (grava e lê os dados em arquivos texto).
- `br.pucminas.matriculas.ui` — `MenuConsole` (interface em linha de comando, com um menu para cada perfil).

## Protótipo (Lab01S03)

### Como executar

```bash
mvn compile
java -cp target/classes br.pucminas.matriculas.Main
```

No primeiro acesso o sistema cria um usuário da secretaria com login `admin` e senha `admin`. A partir dele são cadastrados os cursos, disciplinas, professores e alunos.

### Funcionalidades por perfil

- **Secretaria** — cadastrar cursos, disciplinas, professores, alunos e outros usuários da secretaria; vincular disciplinas a cursos e professores a disciplinas; gerar o currículo do semestre; abrir e encerrar o período de matrículas.
- **Aluno** — ver disciplinas e currículos; matricular-se em até 4 disciplinas obrigatórias e 2 optativas; cancelar matrículas; consultar a situação das matrículas. Matrícula e cancelamento só são aceitos com o período de matrículas aberto.
- **Professor** — listar suas disciplinas e consultar os alunos matriculados em cada uma.

Ao encerrar o período (manualmente pela secretaria ou automaticamente quando a data de fim passa), cada disciplina fica `ATIVA` se tiver pelo menos 3 inscritos ou `CANCELADA` caso contrário. Abrir um novo período inicia um novo semestre: as matrículas anteriores ficam guardadas no período encerrado e as disciplinas voltam a aceitar inscrições.

### Persistência

Os dados são gravados na pasta `dados/` (criada no diretório em que o programa é executado; outro caminho pode ser passado como argumento), em arquivos texto com campos separados por `;`:

| Arquivo | Conteúdo |
|---------|----------|
| `usuarios.txt` | tipo; id; nome; login; senha; matrícula do aluno |
| `disciplinas.txt` | código; nome; capacidade máxima; mínimo para ativar; status; logins dos professores |
| `cursos.txt` | nome; créditos; códigos das disciplinas |
| `curriculos.txt` | semestre; nome do curso |
| `periodos.txt` | início; fim; status |
| `matriculas.txt` | índice do período; login do aluno; código da disciplina; tipo; data; status |
| `cobrancas.txt` | notificações enviadas ao sistema de cobrança (data; matrícula; aluno; código; disciplina) |

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

> Abra o arquivo `docs/diagrama_casos_uso.drawio` em [app.diagrams.net](https://app.diagrams.net/) (File → Open From → Device) para visualizar e editar o diagrama.

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
