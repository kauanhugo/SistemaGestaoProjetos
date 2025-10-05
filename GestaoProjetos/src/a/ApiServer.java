package a;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ApiServer {
    
    private static SistemaGestaoProjetos sistema = new SistemaGestaoProjetos();
    private static ServerSocket serverSocket;
    private static boolean running = true;
    
    public static void main(String[] args) {
        int port = 8080;
        
        try {
            serverSocket = new ServerSocket(port);
            inicializarDadosTesteManual();
            
            System.out.println("Servidor API REST iniciado em http://localhost:" + port);
            System.out.println("Endpoints disponiveis:");
            System.out.println("   GET    /api/health          - Status do servidor");
            System.out.println("   GET    /api/projetos        - Listar projetos");
            System.out.println("   GET    /api/usuarios        - Listar usuarios");
            System.out.println("   POST   /api/login           - Fazer login");
            System.out.println("   POST   /api/logout          - Fazer logout");
            System.out.println("   POST   /api/projetos        - Criar projeto");
            System.out.println("   PUT    /api/projetos/:id/status - Alterar status");
            System.out.println("   POST   /api/projetos/:id/membros - Adicionar membro");
            System.out.println("\nPressione Ctrl+C para parar o servidor");
            
            // Iniciar loop principal do servidor
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleRequest(clientSocket);
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Erro ao aceitar conexao: " + e.getMessage());
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
        } finally {
            stopServer();
        }
    }
    
    private static void inicializarDadosTesteManual() {
        try {
            // Cadastrar usuarios
            sistema.cadastrarUsuario("Administrador", "admin@empresa.com", "admin", "123", SistemaGestaoProjetos.Perfil.ADMIN);
            sistema.cadastrarUsuario("Joao Silva", "joao@empresa.com", "joao", "123", SistemaGestaoProjetos.Perfil.GERENTE);
            sistema.cadastrarUsuario("Maria Santos", "maria@empresa.com", "maria", "123", SistemaGestaoProjetos.Perfil.COLABORADOR);
            sistema.cadastrarUsuario("Pedro Costa", "pedro@empresa.com", "pedro", "123", SistemaGestaoProjetos.Perfil.COLABORADOR);
            
            // Fazer login como admin
            sistema.login("admin", "123");
            
            // Criar projetos de teste
            sistema.criarProjeto("Site Corporativo", "Desenvolvimento do site institucional");
            sistema.criarProjeto("App Mobile", "Aplicativo para iOS e Android");
            sistema.criarProjeto("Sistema ERP", "Sistema de gestao empresarial integrado");
            sistema.criarProjeto("Portal do Cliente", "Area exclusiva para clientes");
            sistema.criarProjeto("Relatorios Gerenciais", "Sistema de relatorios e dashboards");
            
            System.out.println("Dados de teste inicializados com sucesso!");
            
        } catch (Exception e) {
            System.err.println("Erro ao inicializar dados de teste: " + e.getMessage());
        }
    }
    
    private static void handleRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            // Ler requisicao
            String requestLine = in.readLine();
            if (requestLine == null) return;
            
            System.out.println("Requisicao: " + requestLine);
            
            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 2) return;
            
            String method = requestParts[0];
            String path = requestParts[1];
            
            // Ler headers
            Map<String, String> headers = new HashMap<>();
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                String[] headerParts = line.split(":", 2);
                if (headerParts.length == 2) {
                    headers.put(headerParts[0].trim(), headerParts[1].trim());
                }
            }
            
            // Ler body se existir
            StringBuilder body = new StringBuilder();
            if (headers.containsKey("Content-Length")) {
                int contentLength = Integer.parseInt(headers.get("Content-Length"));
                char[] buffer = new char[contentLength];
                in.read(buffer, 0, contentLength);
                body.append(buffer);
            }
            
            // Servir arquivos estáticos do frontend
            if (method.equals("GET") && !path.startsWith("/api")) {
                String staticResponse = serveStaticFile(path);
                if (staticResponse != null) {
                    out.print(staticResponse);
                    out.flush();
                    return;
                }
            }
            
            // Processar requisicao da API
            String response = processRequest(method, path, body.toString());
            
            // Enviar resposta
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: application/json");
            out.println("Access-Control-Allow-Origin: *");
            out.println("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
            out.println("Access-Control-Allow-Headers: Content-Type");
            out.println("Connection: close");
            out.println();
            out.println(response);
            
        } catch (Exception e) {
            System.err.println("Erro ao processar requisicao: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar socket: " + e.getMessage());
            }
        }
    }
    
    private static String serveStaticFile(String path) {
        try {
            // Mapear paths para arquivos estáticos
            if (path.equals("/") || path.equals("")) {
                path = "/index.html";
            }
            
            // Tentar diferentes localizações possíveis
            String[] possiblePaths = {
                "frontend" + path,
                "../frontend" + path,
                "./frontend" + path,
                "src/frontend" + path,
                "bin/frontend" + path
            };
            
            File file = null;
            for (String filePath : possiblePaths) {
                file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    break;
                }
                file = null;
            }
            
            if (file == null) {
                return null;
            }
            
            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            String contentType = getContentType(path);
            
            return "HTTP/1.1 200 OK\r\n" +
                   "Content-Type: " + contentType + "\r\n" +
                   "Content-Length: " + content.length() + "\r\n" +
                   "Access-Control-Allow-Origin: *\r\n" +
                   "\r\n" + content;
        } catch (Exception e) {
            return null;
        }
    }
    
    private static String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg")) return "image/jpeg";
        return "text/plain";
    }
    
    private static String processRequest(String method, String path, String body) {
        try {
            // Health Check
            if (path.equals("/api/health") && method.equals("GET")) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "online");
                response.put("servico", "Gestao de Projetos API");
                response.put("versao", "1.0");
                response.put("timestamp", System.currentTimeMillis());
                return toJson(response);
            }
            
            // Listar projetos
            if (path.equals("/api/projetos") && method.equals("GET")) {
                List<SistemaGestaoProjetos.Projeto> projetos = sistema.getProjetosList();
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("projetos", convertProjetosToMap(projetos));
                response.put("total", projetos.size());
                return toJson(response);
            }
            
            // Listar usuarios
            if (path.equals("/api/usuarios") && method.equals("GET")) {
                List<SistemaGestaoProjetos.Usuario> usuarios = sistema.getUsuariosList();
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("usuarios", convertUsuariosToMap(usuarios));
                response.put("total", usuarios.size());
                return toJson(response);
            }
            
            // Login
            if (path.equals("/api/login") && method.equals("POST")) {
                Map<String, String> credentials = parseJson(body);
                String login = credentials.get("login");
                String senha = credentials.get("senha");
                
                boolean success = sistema.login(login, senha);
                Map<String, Object> response = new HashMap<>();
                
                if (success) {
                    response.put("success", true);
                    response.put("message", "Login realizado com sucesso");
                    response.put("usuario", getUsuarioLogadoInfo());
                } else {
                    response.put("success", false);
                    response.put("message", "Credenciais invalidas");
                }
                
                return toJson(response);
            }
            
            // Logout
            if (path.equals("/api/logout") && method.equals("POST")) {
                sistema.logout();
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Logout realizado com sucesso");
                return toJson(response);
            }
            
            // Criar projeto
            if (path.equals("/api/projetos") && method.equals("POST")) {
                if (sistema.getUsuarioLogado() == null) {
                    return toJsonError("Usuario nao esta logado");
                }
                
                Map<String, String> data = parseJson(body);
                String nome = data.get("nome");
                String descricao = data.get("descricao");
                
                sistema.criarProjeto(nome, descricao);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Projeto criado com sucesso: " + nome);
                return toJson(response);
            }
            
            // Alterar status do projeto
            if (path.startsWith("/api/projetos/") && path.endsWith("/status") && method.equals("PUT")) {
                String[] pathParts = path.split("/");
                if (pathParts.length >= 5) {
                    int projetoId = Integer.parseInt(pathParts[3]);
                    Map<String, String> data = parseJson(body);
                    String statusStr = data.get("status");
                    
                    SistemaGestaoProjetos.Status status = SistemaGestaoProjetos.Status.valueOf(statusStr.toUpperCase());
                    sistema.alterarStatusProjeto(projetoId, status);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Status do projeto alterado para: " + status);
                    return toJson(response);
                }
            }
            
            // Adicionar membro ao projeto
            if (path.startsWith("/api/projetos/") && path.endsWith("/membros") && method.equals("POST")) {
                String[] pathParts = path.split("/");
                if (pathParts.length >= 5) {
                    int projetoId = Integer.parseInt(pathParts[3]);
                    Map<String, String> data = parseJson(body);
                    String loginMembro = data.get("loginMembro");
                    
                    sistema.adicionarMembroProjeto(projetoId, loginMembro);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Membro adicionado ao projeto");
                    return toJson(response);
                }
            }
            
            // CORS Preflight
            if (method.equals("OPTIONS")) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "CORS preflight");
                return toJson(response);
            }
            
            // Endpoint nao encontrado
            return toJsonError("Endpoint nao encontrado: " + method + " " + path);
            
        } catch (Exception e) {
            return toJsonError("Erro interno: " + e.getMessage());
        }
    }
    
    // ========== METODOS AUXILIARES ==========
    
    private static List<Map<String, Object>> convertProjetosToMap(List<SistemaGestaoProjetos.Projeto> projetos) throws Exception {
        List<Map<String, Object>> projetosMap = new ArrayList<>();
        
        for (SistemaGestaoProjetos.Projeto projeto : projetos) {
            Map<String, Object> projetoMap = new HashMap<>();
            
            projetoMap.put("id", projeto.getId());
            projetoMap.put("nome", projeto.getNome());
            projetoMap.put("descricao", projeto.getDescricao());
            projetoMap.put("status", projeto.getStatus().toString());
            projetoMap.put("dataInicio", projeto.getDataInicio().toString());
            projetoMap.put("gerente", projeto.getGerente().getNome());
            projetoMap.put("tamanhoEquipe", projeto.getEquipe().size());
            
            projetosMap.add(projetoMap);
        }
        
        return projetosMap;
    }
    
    private static List<Map<String, Object>> convertUsuariosToMap(List<SistemaGestaoProjetos.Usuario> usuarios) throws Exception {
        List<Map<String, Object>> usuariosMap = new ArrayList<>();
        
        for (SistemaGestaoProjetos.Usuario usuario : usuarios) {
            Map<String, Object> usuarioMap = new HashMap<>();
            
            usuarioMap.put("id", usuario.getId());
            usuarioMap.put("nome", usuario.getNome());
            usuarioMap.put("email", usuario.getEmail());
            usuarioMap.put("login", usuario.getLogin());
            usuarioMap.put("perfil", usuario.getPerfil().toString());
            
            usuariosMap.add(usuarioMap);
        }
        
        return usuariosMap;
    }
    
    private static Map<String, Object> getUsuarioLogadoInfo() {
        SistemaGestaoProjetos.Usuario usuarioLogado = sistema.getUsuarioLogado();
        if (usuarioLogado == null) {
            return null;
        }
        
        Map<String, Object> info = new HashMap<>();
        info.put("nome", usuarioLogado.getNome());
        info.put("login", usuarioLogado.getLogin());
        info.put("perfil", usuarioLogado.getPerfil().toString());
        
        return info;
    }
    
    // ========== MANIPULACAO DE JSON ==========
    
    private static String toJson(Map<String, Object> data) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        
        boolean first = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) json.append(",");
            first = false;
            
            json.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            
            if (value instanceof String) {
                json.append("\"").append(escapeJson((String) value)).append("\"");
            } else if (value instanceof Number || value instanceof Boolean) {
                json.append(value);
            } else if (value instanceof List) {
                json.append(listToJson((List<?>) value));
            } else if (value instanceof Map) {
                json.append(toJson((Map<String, Object>) value));
            } else if (value == null) {
                json.append("null");
            } else {
                json.append("\"").append(escapeJson(value.toString())).append("\"");
            }
        }
        
        json.append("}");
        return json.toString();
    }
    
    private static String listToJson(List<?> list) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        
        boolean first = true;
        for (Object item : list) {
            if (!first) json.append(",");
            first = false;
            
            if (item instanceof Map) {
                json.append(toJson((Map<String, Object>) item));
            } else if (item instanceof String) {
                json.append("\"").append(escapeJson((String) item)).append("\"");
            } else {
                json.append(item);
            }
        }
        
        json.append("]");
        return json.toString();
    }
    
    private static String toJsonError(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return toJson(response);
    }
    
    private static Map<String, String> parseJson(String json) {
        Map<String, String> result = new HashMap<>();
        if (json == null || json.trim().isEmpty()) return result;
        
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
            String[] pairs = json.split(",");
            
            for (String pair : pairs) {
                String[] keyValue = pair.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim().replace("\"", "");
                    result.put(key, value);
                }
            }
        }
        
        return result;
    }
    
    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    private static void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Erro ao parar servidor: " + e.getMessage());
        }
        System.out.println("Servidor parado");
    }
    
    // Shutdown hook para parar o servidor graciosamente
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nParando servidor...");
            stopServer();
        }));
    }
}