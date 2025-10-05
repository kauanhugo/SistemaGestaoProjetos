package a;

import java.util.*;
import java.time.LocalDate;

public class SistemaGestaoProjetos {
    // Enums internos expandidos
    public enum Perfil { ADMIN, GERENTE, COLABORADOR }
    public enum Status { PLANEJADO, EM_ANDAMENTO, CONCLUIDO, CANCELADO, PAUSADO }
    public enum Prioridade { BAIXA, MEDIA, ALTA, URGENTE }
    public enum Categoria { 
        DESENVOLVIMENTO, MARKETING, VENDAS, RH, FINANCEIRO, 
        OPERACOES, PESQUISA, QUALIDADE, OUTRO 
    }
    
    // Enum para StatusTarefa movido para fora da classe Tarefa
    public enum StatusTarefa { 
        PENDENTE, EM_ANDAMENTO, CONCLUIDA, CANCELADA, BLOQUEADA 
    }
    
    // Classe Usuario expandida
    class Usuario {
        private static int nextId = 1;
        private int id;
        private String nome;
        private String email;
        private String login;
        private String senha;
        private Perfil perfil;
        private LocalDate dataCadastro;
        private boolean ativo;
        
        public Usuario(String nome, String email, String login, String senha, Perfil perfil) {
            this.id = nextId++;
            this.nome = nome;
            this.email = email;
            this.login = login;
            this.senha = senha;
            this.perfil = perfil;
            this.dataCadastro = LocalDate.now();
            this.ativo = true;
        }
        
        // Getters e Setters
        public int getId() { return id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getLogin() { return login; }
        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
        public Perfil getPerfil() { return perfil; }
        public void setPerfil(Perfil perfil) { this.perfil = perfil; }
        public LocalDate getDataCadastro() { return dataCadastro; }
        public boolean isAtivo() { return ativo; }
        public void setAtivo(boolean ativo) { this.ativo = ativo; }
        
        @Override
        public String toString() {
            return nome + " (" + perfil + ")";
        }
        
        public boolean validarSenha(String senha) {
            return this.senha.equals(senha);
        }
    }
    
    // Classe Projeto expandida
    class Projeto {
        private static int nextId = 1;
        private int id;
        private String nome;
        private String descricao;
        private LocalDate dataInicio;
        private LocalDate dataTermino;
        private LocalDate prazoFinal;
        private Status status;
        private Prioridade prioridade;
        private Categoria categoria;
        private double orcamento;
        private Usuario gerente;
        private List<Usuario> equipe;
        private List<Tarefa> tarefas;
        private Map<String, String> metadados;
        
        public Projeto(String nome, String descricao, Usuario gerente) {
            this.id = nextId++;
            this.nome = nome;
            this.descricao = descricao;
            this.dataInicio = LocalDate.now();
            this.status = Status.PLANEJADO;
            this.prioridade = Prioridade.MEDIA;
            this.categoria = Categoria.OUTRO;
            this.orcamento = 0.0;
            this.gerente = gerente;
            this.equipe = new ArrayList<>();
            this.tarefas = new ArrayList<>();
            this.metadados = new HashMap<>();
            this.equipe.add(gerente);
            
            // Prazo padrão: 1 mês a partir de hoje
            this.prazoFinal = LocalDate.now().plusMonths(1);
        }
        
        // Construtor completo
        public Projeto(String nome, String descricao, Usuario gerente, Prioridade prioridade, 
                      Categoria categoria, double orcamento, LocalDate prazoFinal) {
            this(nome, descricao, gerente);
            this.prioridade = prioridade;
            this.categoria = categoria;
            this.orcamento = orcamento;
            this.prazoFinal = prazoFinal;
        }
        
        // Getters e Setters
        public int getId() { return id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
        public Status getStatus() { return status; }
        public void setStatus(Status status) { this.status = status; }
        public Usuario getGerente() { return gerente; }
        public List<Usuario> getEquipe() { return equipe; }
        public LocalDate getDataInicio() { return dataInicio; }
        public LocalDate getDataTermino() { return dataTermino; }
        public void setDataTermino(LocalDate data) { this.dataTermino = data; }
        public LocalDate getPrazoFinal() { return prazoFinal; }
        public void setPrazoFinal(LocalDate prazoFinal) { this.prazoFinal = prazoFinal; }
        public Prioridade getPrioridade() { return prioridade; }
        public void setPrioridade(Prioridade prioridade) { this.prioridade = prioridade; }
        public Categoria getCategoria() { return categoria; }
        public void setCategoria(Categoria categoria) { this.categoria = categoria; }
        public double getOrcamento() { return orcamento; }
        public void setOrcamento(double orcamento) { this.orcamento = orcamento; }
        public List<Tarefa> getTarefas() { return tarefas; }
        public Map<String, String> getMetadados() { return metadados; }
        
        public void adicionarMembroEquipe(Usuario usuario) {
            if (!equipe.contains(usuario)) {
                equipe.add(usuario);
                System.out.println(usuario.getNome() + " adicionado a equipe do projeto " + nome);
            }
        }
        
        public void removerMembroEquipe(Usuario usuario) {
            if (equipe.contains(usuario) && !usuario.equals(gerente)) {
                equipe.remove(usuario);
                System.out.println(usuario.getNome() + " removido da equipe do projeto " + nome);
            }
        }
        
        public void adicionarTarefa(Tarefa tarefa) {
            tarefas.add(tarefa);
        }
        
        public int getProgresso() {
            if (tarefas.isEmpty()) return 0;
            
            long concluidas = tarefas.stream().filter(t -> t.getStatus() == StatusTarefa.CONCLUIDA).count();
            return (int) ((concluidas * 100) / tarefas.size());
        }
        
        public boolean estaAtrasado() {
            return LocalDate.now().isAfter(prazoFinal) && status != Status.CONCLUIDO;
        }
        
        public long getDiasRestantes() {
            return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), prazoFinal);
        }
        
        @Override
        public String toString() {
            return "Projeto " + id + ": " + nome + " - " + status + " (Gerente: " + gerente.getNome() + ")";
        }
    }
    
    // Nova classe Tarefa
    class Tarefa {
        private static int nextId = 1;
        private int id;
        private String titulo;
        private String descricao;
        private StatusTarefa status;
        private Prioridade prioridade;
        private Usuario responsavel;
        private LocalDate dataCriacao;
        private LocalDate dataConclusao;
        private LocalDate prazo;
        private int projetoId;
        
        public Tarefa(String titulo, String descricao, Usuario responsavel, int projetoId) {
            this.id = nextId++;
            this.titulo = titulo;
            this.descricao = descricao;
            this.status = StatusTarefa.PENDENTE;
            this.prioridade = Prioridade.MEDIA;
            this.responsavel = responsavel;
            this.dataCriacao = LocalDate.now();
            this.projetoId = projetoId;
            this.prazo = LocalDate.now().plusDays(7); // Prazo padrão: 7 dias
        }
        
        // Getters e Setters
        public int getId() { return id; }
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
        public StatusTarefa getStatus() { return status; }
        public void setStatus(StatusTarefa status) { this.status = status; }
        public Prioridade getPrioridade() { return prioridade; }
        public void setPrioridade(Prioridade prioridade) { this.prioridade = prioridade; }
        public Usuario getResponsavel() { return responsavel; }
        public void setResponsavel(Usuario responsavel) { this.responsavel = responsavel; }
        public LocalDate getDataCriacao() { return dataCriacao; }
        public LocalDate getDataConclusao() { return dataConclusao; }
        public void setDataConclusao(LocalDate data) { this.dataConclusao = data; }
        public LocalDate getPrazo() { return prazo; }
        public void setPrazo(LocalDate prazo) { this.prazo = prazo; }
        public int getProjetoId() { return projetoId; }
        
        public boolean estaAtrasada() {
            return LocalDate.now().isAfter(prazo) && status != StatusTarefa.CONCLUIDA;
        }
        
        public void concluir() {
            this.status = StatusTarefa.CONCLUIDA;
            this.dataConclusao = LocalDate.now();
        }
        
        @Override
        public String toString() {
            return "Tarefa " + id + ": " + titulo + " - " + status + " (Responsavel: " + responsavel.getNome() + ")";
        }
    }
    
    // Classe para relatórios
    class Relatorio {
        private LocalDate dataGeracao;
        private String tipo;
        private Map<String, Object> dados;
        
        public Relatorio(String tipo) {
            this.dataGeracao = LocalDate.now();
            this.tipo = tipo;
            this.dados = new HashMap<>();
        }
        
        public void adicionarDado(String chave, Object valor) {
            dados.put(chave, valor);
        }
        
        public void gerarRelatorioProjetos() {
            // Implementação para gerar relatório de projetos
        }
        
        public void gerarRelatorioUsuarios() {
            // Implementação para gerar relatório de usuários
        }
    }
    
    // Listas para armazenamento em memoria
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Projeto> projetos = new ArrayList<>();
    private List<Tarefa> tarefas = new ArrayList<>();
    private Usuario usuarioLogado = null;
    
    // Metodos de autenticacao e usuario
    public void cadastrarUsuario(String nome, String email, String login, String senha, Perfil perfil) {
        // Verificar se login ja existe
        for (Usuario usuario : usuarios) {
            if (usuario.getLogin().equals(login)) {
                System.out.println("Erro: Login ja existe!");
                return;
            }
        }
        
        Usuario novoUsuario = new Usuario(nome, email, login, senha, perfil);
        usuarios.add(novoUsuario);
        System.out.println("Usuario cadastrado: " + nome + " (" + perfil + ")");
    }
    
    public boolean login(String login, String senha) {
        for (Usuario usuario : usuarios) {
            if (usuario.getLogin().equals(login) && usuario.validarSenha(senha) && usuario.isAtivo()) {
                usuarioLogado = usuario;
                System.out.println("Login realizado: " + usuario.getNome() + " (" + usuario.getPerfil() + ")");
                return true;
            }
        }
        System.out.println("Login falhou: credenciais invalidas ou usuario inativo");
        return false;
    }
    
    public void logout() {
        if (usuarioLogado != null) {
            System.out.println("Logout realizado: " + usuarioLogado.getNome());
            usuarioLogado = null;
        }
    }
    
    public void alterarSenha(String senhaAtual, String novaSenha) {
        if (usuarioLogado == null) {
            System.out.println("Erro: Faca login primeiro");
            return;
        }
        
        if (usuarioLogado.validarSenha(senhaAtual)) {
            usuarioLogado.setSenha(novaSenha);
            System.out.println("Senha alterada com sucesso");
        } else {
            System.out.println("Senha atual incorreta");
        }
    }
    
    // Metodos de projeto
    public void criarProjeto(String nome, String descricao) {
        criarProjeto(nome, descricao, Prioridade.MEDIA, Categoria.OUTRO, 0.0, null);
    }
    
    public void criarProjeto(String nome, String descricao, Prioridade prioridade, 
                           Categoria categoria, double orcamento, LocalDate prazoFinal) {
        if (usuarioLogado == null) {
            System.out.println("Erro: Faca login primeiro");
            return;
        }
        
        if (usuarioLogado.getPerfil() == Perfil.ADMIN || usuarioLogado.getPerfil() == Perfil.GERENTE) {
            Projeto novoProjeto;
            
            if (prazoFinal != null) {
                novoProjeto = new Projeto(nome, descricao, usuarioLogado, prioridade, categoria, orcamento, prazoFinal);
            } else {
                novoProjeto = new Projeto(nome, descricao, usuarioLogado);
                novoProjeto.setPrioridade(prioridade);
                novoProjeto.setCategoria(categoria);
                novoProjeto.setOrcamento(orcamento);
            }
            
            projetos.add(novoProjeto);
            System.out.println("Projeto criado: " + nome + " (ID: " + novoProjeto.getId() + ")");
        } else {
            System.out.println("Apenas administradores e gerentes podem criar projetos");
        }
    }
    
    public void alterarStatusProjeto(int idProjeto, Status novoStatus) {
        if (usuarioLogado == null) {
            System.out.println("Erro: Faca login primeiro");
            return;
        }
        
        Projeto projeto = buscarProjetoPorId(idProjeto);
        if (projeto == null) {
            System.out.println("Projeto nao encontrado");
            return;
        }
        
        // Verificar permissao
        if (usuarioLogado.getPerfil() == Perfil.ADMIN || 
            projeto.getGerente().getId() == usuarioLogado.getId()) {
            
            Status statusAnterior = projeto.getStatus();
            projeto.setStatus(novoStatus);
            
            // Se concluído, definir data de término
            if (novoStatus == Status.CONCLUIDO) {
                projeto.setDataTermino(LocalDate.now());
            }
            
            System.out.println("Status do projeto '" + projeto.getNome() + "' alterado: " + 
                             statusAnterior + " -> " + novoStatus);
        } else {
            System.out.println("Apenas o gerente do projeto ou administrador podem alterar o status");
        }
    }
    
    public void adicionarMembroProjeto(int idProjeto, String loginMembro) {
        if (usuarioLogado == null) {
            System.out.println("Erro: Faca login primeiro");
            return;
        }
        
        Projeto projeto = buscarProjetoPorId(idProjeto);
        Usuario membro = buscarUsuarioPorLogin(loginMembro);
        
        if (projeto == null || membro == null) {
            System.out.println("Projeto ou usuario nao encontrado");
            return;
        }
        
        // Verificar permissao
        if (usuarioLogado.getPerfil() == Perfil.ADMIN || 
            projeto.getGerente().getId() == usuarioLogado.getId()) {
            
            projeto.adicionarMembroEquipe(membro);
        } else {
            System.out.println("Apenas o gerente do projeto ou administrador podem adicionar membros");
        }
    }
    
    public void removerMembroProjeto(int idProjeto, String loginMembro) {
        if (usuarioLogado == null) {
            System.out.println("Erro: Faca login primeiro");
            return;
        }
        
        Projeto projeto = buscarProjetoPorId(idProjeto);
        Usuario membro = buscarUsuarioPorLogin(loginMembro);
        
        if (projeto == null || membro == null) {
            System.out.println("Projeto ou usuario nao encontrado");
            return;
        }
        
        // Nao permitir remover o gerente
        if (membro.equals(projeto.getGerente())) {
            System.out.println("Nao e possivel remover o gerente do projeto");
            return;
        }
        
        // Verificar permissao
        if (usuarioLogado.getPerfil() == Perfil.ADMIN || 
            projeto.getGerente().getId() == usuarioLogado.getId()) {
            
            projeto.removerMembroEquipe(membro);
        } else {
            System.out.println("Apenas o gerente do projeto ou administrador podem remover membros");
        }
    }
    
    // Metodos de tarefa
    public void criarTarefa(int idProjeto, String titulo, String descricao, String loginResponsavel) {
        if (usuarioLogado == null) {
            System.out.println("Erro: Faca login primeiro");
            return;
        }
        
        Projeto projeto = buscarProjetoPorId(idProjeto);
        Usuario responsavel = buscarUsuarioPorLogin(loginResponsavel);
        
        if (projeto == null || responsavel == null) {
            System.out.println("Projeto ou usuario nao encontrado");
            return;
        }
        
        // Verificar se o responsavel esta na equipe do projeto
        if (!projeto.getEquipe().contains(responsavel)) {
            System.out.println("O responsavel deve ser membro da equipe do projeto");
            return;
        }
        
        // Verificar permissao
        if (usuarioLogado.getPerfil() == Perfil.ADMIN || 
            projeto.getGerente().getId() == usuarioLogado.getId() ||
            usuarioLogado.equals(responsavel)) {
            
            Tarefa novaTarefa = new Tarefa(titulo, descricao, responsavel, idProjeto);
            projeto.adicionarTarefa(novaTarefa);
            tarefas.add(novaTarefa);
            System.out.println("Tarefa criada: " + titulo + " (ID: " + novaTarefa.getId() + ")");
        } else {
            System.out.println("Sem permissao para criar tarefas neste projeto");
        }
    }
    
    public void alterarStatusTarefa(int idTarefa, StatusTarefa novoStatus) {
        if (usuarioLogado == null) {
            System.out.println("Erro: Faca login primeiro");
            return;
        }
        
        Tarefa tarefa = buscarTarefaPorId(idTarefa);
        if (tarefa == null) {
            System.out.println("Tarefa nao encontrada");
            return;
        }
        
        // Verificar permissao: responsavel da tarefa, gerente do projeto ou admin
        Projeto projeto = buscarProjetoPorId(tarefa.getProjetoId());
        if (projeto == null) {
            System.out.println("Projeto da tarefa nao encontrado");
            return;
        }
        
        if (usuarioLogado.getPerfil() == Perfil.ADMIN || 
            projeto.getGerente().getId() == usuarioLogado.getId() ||
            tarefa.getResponsavel().getId() == usuarioLogado.getId()) {
            
            StatusTarefa statusAnterior = tarefa.getStatus();
            tarefa.setStatus(novoStatus);
            
            // Se concluída, definir data de conclusão
            if (novoStatus == StatusTarefa.CONCLUIDA) {
                tarefa.setDataConclusao(LocalDate.now());
            }
            
            System.out.println("Status da tarefa '" + tarefa.getTitulo() + "' alterado: " + 
                             statusAnterior + " -> " + novoStatus);
        } else {
            System.out.println("Sem permissao para alterar esta tarefa");
        }
    }
    
    // Metodos de relatorio
    public void gerarRelatorioProjetos() {
        System.out.println("\n=== RELATORIO DE PROJETOS ===");
        System.out.println("Total de projetos: " + projetos.size());
        System.out.println("Projetos por status:");
        
        Map<String, Long> projetosPorStatus = new HashMap<>();
        for (Status status : Status.values()) {
            long count = projetos.stream().filter(p -> p.getStatus() == status).count();
            projetosPorStatus.put(status.toString(), count);
        }
        
        for (Map.Entry<String, Long> entry : projetosPorStatus.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        
        // Projetos atrasados
        long atrasados = projetos.stream().filter(Projeto::estaAtrasado).count();
        System.out.println("Projetos atrasados: " + atrasados);
        
        // Projetos por categoria
        System.out.println("\nProjetos por categoria:");
        Map<String, Long> projetosPorCategoria = new HashMap<>();
        for (Categoria categoria : Categoria.values()) {
            long count = projetos.stream().filter(p -> p.getCategoria() == categoria).count();
            if (count > 0) {
                System.out.println("  " + categoria + ": " + count);
            }
        }
    }
    
    public void gerarRelatorioUsuarios() {
        System.out.println("\n=== RELATORIO DE USUARIOS ===");
        System.out.println("Total de usuarios: " + usuarios.size());
        
        Map<String, Long> usuariosPorPerfil = new HashMap<>();
        for (Perfil perfil : Perfil.values()) {
            long count = usuarios.stream().filter(u -> u.getPerfil() == perfil).count();
            usuariosPorPerfil.put(perfil.toString(), count);
        }
        
        System.out.println("Usuarios por perfil:");
        for (Map.Entry<String, Long> entry : usuariosPorPerfil.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        
        long usuariosAtivos = usuarios.stream().filter(Usuario::isAtivo).count();
        System.out.println("Usuarios ativos: " + usuariosAtivos);
    }
    
    // Metodos de consulta
    public void listarProjetos() {
        if (projetos.isEmpty()) {
            System.out.println("Nenhum projeto cadastrado");
            return;
        }
        
        System.out.println("\n--- LISTA DE PROJETOS (" + projetos.size() + ") ---");
        for (Projeto projeto : projetos) {
            String atrasado = projeto.estaAtrasado() ? " [ATRASADO]" : "";
            System.out.println("ID: " + projeto.getId() + 
                             " | Nome: " + projeto.getNome() + 
                             " | Status: " + projeto.getStatus() + 
                             " | Prioridade: " + projeto.getPrioridade() +
                             " | Progresso: " + projeto.getProgresso() + "%" +
                             " | Gerente: " + projeto.getGerente().getNome() +
                             " | Equipe: " + projeto.getEquipe().size() + " membros" +
                             atrasado);
        }
    }
    
    public void listarProjetosPorStatus(Status status) {
        List<Projeto> projetosFiltrados = projetos.stream()
            .filter(p -> p.getStatus() == status)
            .toList();
            
        if (projetosFiltrados.isEmpty()) {
            System.out.println("Nenhum projeto com status: " + status);
            return;
        }
        
        System.out.println("\n--- PROJETOS " + status + " (" + projetosFiltrados.size() + ") ---");
        for (Projeto projeto : projetosFiltrados) {
            System.out.println("ID: " + projeto.getId() + " | Nome: " + projeto.getNome() + 
                             " | Prioridade: " + projeto.getPrioridade());
        }
    }
    
    public void listarUsuarios() {
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuario cadastrado");
            return;
        }
        
        System.out.println("\n--- LISTA DE USUARIOS (" + usuarios.size() + ") ---");
        for (Usuario usuario : usuarios) {
            String status = usuario.isAtivo() ? "Ativo" : "Inativo";
            System.out.println("ID: " + usuario.getId() + 
                             " | Nome: " + usuario.getNome() + 
                             " | Login: " + usuario.getLogin() + 
                             " | Perfil: " + usuario.getPerfil() +
                             " | Status: " + status);
        }
    }
    
    public void listarTarefasProjeto(int idProjeto) {
        Projeto projeto = buscarProjetoPorId(idProjeto);
        if (projeto == null) {
            System.out.println("Projeto nao encontrado");
            return;
        }
        
        List<Tarefa> tarefasProjeto = projeto.getTarefas();
        if (tarefasProjeto.isEmpty()) {
            System.out.println("Nenhuma tarefa no projeto " + projeto.getNome());
            return;
        }
        
        System.out.println("\n--- TAREFAS DO PROJETO: " + projeto.getNome() + " ---");
        for (Tarefa tarefa : tarefasProjeto) {
            String atrasada = tarefa.estaAtrasada() ? " [ATRASADA]" : "";
            System.out.println("ID: " + tarefa.getId() + 
                             " | Titulo: " + tarefa.getTitulo() + 
                             " | Status: " + tarefa.getStatus() + 
                             " | Prioridade: " + tarefa.getPrioridade() +
                             " | Responsavel: " + tarefa.getResponsavel().getNome() +
                             " | Prazo: " + tarefa.getPrazo() +
                             atrasada);
        }
    }
    
    // Metodos auxiliares
    private Projeto buscarProjetoPorId(int id) {
        for (Projeto projeto : projetos) {
            if (projeto.getId() == id) {
                return projeto;
            }
        }
        return null;
    }
    
    private Usuario buscarUsuarioPorLogin(String login) {
        for (Usuario usuario : usuarios) {
            if (usuario.getLogin().equals(login)) {
                return usuario;
            }
        }
        return null;
    }
    
    private Tarefa buscarTarefaPorId(int id) {
        for (Tarefa tarefa : tarefas) {
            if (tarefa.getId() == id) {
                return tarefa;
            }
        }
        return null;
    }
    
    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
    
    // Metodos para API
    public List<Usuario> getUsuariosList() {
        return new ArrayList<>(usuarios);
    }
    
    public List<Projeto> getProjetosList() {
        return new ArrayList<>(projetos);
    }
    
    public List<Tarefa> getTarefasList() {
        return new ArrayList<>(tarefas);
    }
    
    // Metodos de estatisticas
    public Map<String, Object> getEstatisticas() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalUsuarios", usuarios.size());
        stats.put("totalProjetos", projetos.size());
        stats.put("totalTarefas", tarefas.size());
        
        // Projetos por status
        Map<String, Long> projetosStatus = new HashMap<>();
        for (Status status : Status.values()) {
            long count = projetos.stream().filter(p -> p.getStatus() == status).count();
            projetosStatus.put(status.toString(), count);
        }
        stats.put("projetosPorStatus", projetosStatus);
        
        // Projetos atrasados
        long projetosAtrasados = projetos.stream().filter(Projeto::estaAtrasado).count();
        stats.put("projetosAtrasados", projetosAtrasados);
        
        // Tarefas por status
        Map<String, Long> tarefasStatus = new HashMap<>();
        for (StatusTarefa status : StatusTarefa.values()) {
            long count = tarefas.stream().filter(t -> t.getStatus() == status).count();
            tarefasStatus.put(status.toString(), count);
        }
        stats.put("tarefasPorStatus", tarefasStatus);
        
        return stats;
    }
    
    // Metodo principal para teste
    public static void main(String[] args) {
        SistemaGestaoProjetos sistema = new SistemaGestaoProjetos();
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== SISTEMA DE GESTAO DE PROJETOS AVANCADO ===\n");
        
        // Dados iniciais para teste
        sistema.inicializarDadosTeste();
        
        // Menu interativo
        boolean executando = true;
        while (executando) {
            sistema.mostrarMenu();
            int opcao = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer
            
            switch (opcao) {
                case 1:
                    sistema.menuLogin(scanner);
                    break;
                case 2:
                    sistema.listarProjetos();
                    break;
                case 3:
                    sistema.listarUsuarios();
                    break;
                case 4:
                    sistema.menuCriarProjeto(scanner);
                    break;
                case 5:
                    sistema.menuAlterarStatus(scanner);
                    break;
                case 6:
                    sistema.menuAdicionarMembro(scanner);
                    break;
                case 7:
                    sistema.menuCriarTarefa(scanner);
                    break;
                case 8:
                    sistema.gerarRelatorioProjetos();
                    break;
                case 9:
                    sistema.gerarRelatorioUsuarios();
                    break;
                case 10:
                    sistema.menuEstatisticas();
                    break;
                case 0:
                    executando = false;
                    System.out.println("Saindo do sistema...");
                    break;
                default:
                    System.out.println("Opcao invalida!");
            }
        }
        
        scanner.close();
    }
    
    private void inicializarDadosTeste() {
        // Cadastrar usuarios de teste
        cadastrarUsuario("Administrador", "admin@empresa.com", "admin", "123", Perfil.ADMIN);
        cadastrarUsuario("Joao Silva", "joao@empresa.com", "joao", "123", Perfil.GERENTE);
        cadastrarUsuario("Maria Santos", "maria@empresa.com", "maria", "123", Perfil.COLABORADOR);
        cadastrarUsuario("Pedro Costa", "pedro@empresa.com", "pedro", "123", Perfil.COLABORADOR);
        cadastrarUsuario("Ana Oliveira", "ana@empresa.com", "ana", "123", Perfil.COLABORADOR);
        
        // Login automatico como admin
        login("admin", "123");
        
        // Criar projetos de teste
        criarProjeto("Site Corporativo", "Desenvolvimento do site institucional", 
                    Prioridade.ALTA, Categoria.DESENVOLVIMENTO, 50000.0, LocalDate.now().plusMonths(2));
        criarProjeto("App Mobile", "Aplicativo para iOS e Android", 
                    Prioridade.MEDIA, Categoria.DESENVOLVIMENTO, 75000.0, LocalDate.now().plusMonths(3));
        criarProjeto("Sistema ERP", "Sistema de gestao empresarial integrado",
                    Prioridade.ALTA, Categoria.DESENVOLVIMENTO, 150000.0, LocalDate.now().plusMonths(6));
        
        // Adicionar membros aos projetos
        adicionarMembroProjeto(1, "maria");
        adicionarMembroProjeto(1, "pedro");
        adicionarMembroProjeto(2, "ana");
        
        // Criar algumas tarefas
        criarTarefa(1, "Design do layout", "Criar design do site corporativo", "maria");
        criarTarefa(1, "Desenvolvimento frontend", "Implementar interface do usuário", "pedro");
        criarTarefa(2, "Prototipo do app", "Desenvolver prototipo navegavel", "ana");
        
        // Concluir uma tarefa para teste de progresso
        alterarStatusTarefa(1, StatusTarefa.CONCLUIDA);
    }
    
    private void mostrarMenu() {
        System.out.println("\n=== MENU PRINCIPAL ===");
        if (usuarioLogado != null) {
            System.out.println("Usuario: " + usuarioLogado.getNome() + " (" + usuarioLogado.getPerfil() + ")");
        }
        System.out.println("1 - Login");
        System.out.println("2 - Listar Projetos");
        System.out.println("3 - Listar Usuarios");
        System.out.println("4 - Criar Projeto");
        System.out.println("5 - Alterar Status do Projeto");
        System.out.println("6 - Adicionar Membro ao Projeto");
        System.out.println("7 - Criar Tarefa");
        System.out.println("8 - Relatorio de Projetos");
        System.out.println("9 - Relatorio de Usuarios");
        System.out.println("10 - Estatisticas");
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opcao: ");
    }
    
    private void menuLogin(Scanner scanner) {
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();
        login(login, senha);
    }
    
    private void menuCriarProjeto(Scanner scanner) {
        System.out.print("Nome do projeto: ");
        String nome = scanner.nextLine();
        System.out.print("Descricao: ");
        String descricao = scanner.nextLine();
        
        System.out.println("Prioridade (1-BAIXA, 2-MEDIA, 3-ALTA, 4-URGENTE): ");
        int prioridadeOpcao = scanner.nextInt();
        scanner.nextLine();
        
        Prioridade prioridade = Prioridade.MEDIA;
        switch (prioridadeOpcao) {
            case 1: prioridade = Prioridade.BAIXA; break;
            case 2: prioridade = Prioridade.MEDIA; break;
            case 3: prioridade = Prioridade.ALTA; break;
            case 4: prioridade = Prioridade.URGENTE; break;
        }
        
        System.out.print("Orcamento: ");
        double orcamento = scanner.nextDouble();
        scanner.nextLine();
        
        criarProjeto(nome, descricao, prioridade, Categoria.DESENVOLVIMENTO, orcamento, null);
    }
    
    private void menuAlterarStatus(Scanner scanner) {
        listarProjetos();
        System.out.print("ID do projeto: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        
        System.out.println("Status disponiveis:");
        System.out.println("1 - PLANEJADO");
        System.out.println("2 - EM_ANDAMENTO");
        System.out.println("3 - CONCLUIDO");
        System.out.println("4 - CANCELADO");
        System.out.println("5 - PAUSADO");
        System.out.print("Escolha o status: ");
        int statusOpcao = scanner.nextInt();
        scanner.nextLine();
        
        Status novoStatus;
        switch (statusOpcao) {
            case 1: novoStatus = Status.PLANEJADO; break;
            case 2: novoStatus = Status.EM_ANDAMENTO; break;
            case 3: novoStatus = Status.CONCLUIDO; break;
            case 4: novoStatus = Status.CANCELADO; break;
            case 5: novoStatus = Status.PAUSADO; break;
            default: 
                System.out.println("Status invalido!");
                return;
        }
        
        alterarStatusProjeto(id, novoStatus);
    }
    
    private void menuAdicionarMembro(Scanner scanner) {
        listarProjetos();
        System.out.print("ID do projeto: ");
        int idProjeto = scanner.nextInt();
        scanner.nextLine();
        
        listarUsuarios();
        System.out.print("Login do membro: ");
        String loginMembro = scanner.nextLine();
        
        adicionarMembroProjeto(idProjeto, loginMembro);
    }
    
    private void menuCriarTarefa(Scanner scanner) {
        listarProjetos();
        System.out.print("ID do projeto: ");
        int idProjeto = scanner.nextInt();
        scanner.nextLine();
        
        System.out.print("Titulo da tarefa: ");
        String titulo = scanner.nextLine();
        
        System.out.print("Descricao da tarefa: ");
        String descricao = scanner.nextLine();
        
        listarUsuarios();
        System.out.print("Login do responsavel: ");
        String loginResponsavel = scanner.nextLine();
        
        criarTarefa(idProjeto, titulo, descricao, loginResponsavel);
    }
    
    private void menuEstatisticas() {
        Map<String, Object> stats = getEstatisticas();
        System.out.println("\n=== ESTATISTICAS DO SISTEMA ===");
        System.out.println("Total de Usuarios: " + stats.get("totalUsuarios"));
        System.out.println("Total de Projetos: " + stats.get("totalProjetos"));
        System.out.println("Total de Tarefas: " + stats.get("totalTarefas"));
        System.out.println("Projetos Atrasados: " + stats.get("projetosAtrasados"));
    }
}