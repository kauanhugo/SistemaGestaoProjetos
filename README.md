# Sistema de Gestão de Projetos - GestãoPro

![GestãoPro](https://img.shields.io/badge/Status-Ativo-success) ![Versão](https://img.shields.io/badge/Versão-1.0-blue) ![Licença](https://img.shields.io/badge/Licença-MIT-green)

## 📋 Descrição

O **GestãoPro** é uma plataforma web completa para gerenciamento de projetos, equipes e tarefas. Desenvolvido com tecnologias modernas, oferece uma interface intuitiva e funcionalidades avançadas para otimizar o fluxo de trabalho em equipe.

## ✨ Funcionalidades

### 🏠 Dashboard
- **Visão geral** dos projetos ativos
- **Estatísticas** em tempo real
- **Atividades recentes** da equipe
- **Métricas** de desempenho

### 📊 Gestão de Projetos
- ✅ **Criação e edição** de projetos
- ✅ **Definição de prioridades** (Baixa, Média, Alta)
- ✅ **Atribuição de equipes**
- ✅ **Controle de orçamento**
- ✅ **Acompanhamento de progresso**
- ✅ **Categorização** por áreas

### 👥 Gestão de Equipe
- ✅ **Cadastro de membros**
- ✅ **Atribuição de habilidades**
- ✅ **Controle de status** (Ativo, Inativo, Férias)
- ✅ **Perfis de usuário**

### 📅 Calendário
- ✅ **Agendamento de eventos**
- ✅ **Visualização mensal**
- ✅ **Cores categorizadas**
- ✅ **Navegação intuitiva**

### 📈 Relatórios
- ✅ **Gráficos interativos**
- ✅ **Métricas de desempenho**
- ✅ **Filtros personalizáveis**
- ✅ **Exportação de dados**

### ✅ Sistema de Tarefas
- ✅ **Criação e atribuição** de tarefas
- ✅ **Definição de prazos**
- ✅ **Controle de prioridades**
- ✅ **Acompanhamento de status**

## 🚀 Tecnologias Utilizadas

- **Frontend:** HTML5, CSS3, JavaScript (ES6+)
- **Estilização:** CSS Custom Properties, Grid, Flexbox
- **Gráficos:** Chart.js
- **Ícones:** Font Awesome 6
- **Design:** Interface moderna e responsiva

## 🛠️ Instalação e Uso

### Pré-requisitos
- Navegador web moderno (Chrome, Firefox, Safari, Edge)
- Conexão com internet (para carregar CDNs)

### Execução
1. **Clone o repositório:**
   ```bash
   git clone https://github.com/seu-usuario/gestaopro.git
   ```

2. **Abra o arquivo:**
   ```bash
   cd gestaopro
   open index.html
   ```
   Ou simplesmente arraste o arquivo `index.html` para o navegador.

3. **Login:**
   - **Usuário:** `admin`
   - **Senha:** `123456`

## 🎯 Estrutura do Projeto

```
gestaopro/
│
├── index.html              # Arquivo principal
├── README.md              # Documentação
└── assets/               # (Opcional) Diretório para assets
    ├── css/
    ├── js/
    └── images/
```

## 📖 Como Usar

### 1. **Dashboard**
- Acesse estatísticas rápidas do sistema
- Veja atividades recentes da equipe
- Monitore o progresso geral

### 2. **Gerenciar Projetos**
- Clique em "Projetos" no menu lateral
- Use "+ Novo Projeto" para criar
- Clique em um projeto para ver detalhes
- Use o breadcrumb para navegar

### 3. **Adicionar Tarefas**
- Acesse os detalhes de um projeto
- Clique em "+ Adicionar Tarefa"
- Preencha as informações necessárias
- Atribua a um membro da equipe

### 4. **Gerenciar Equipe**
- Navegue para "Equipe"
- Adicione novos membros
- Defina habilidades e cargos
- Controle status de disponibilidade

### 5. **Calendário**
- Acesse "Calendário"
- Adicione eventos e reuniões
- Use os controles para navegar entre meses

## 🔧 Personalização

### Cores do Tema
As cores podem ser personalizadas editando as variáveis CSS no início do arquivo:

```css
:root {
    --primary-color: #3498db;
    --secondary-color: #2980b9;
    --success-color: #27ae60;
    --warning-color: #f39c12;
    --danger-color: #e74c3c;
    /* ... */
}
```

### Dados Iniciais
Os dados de exemplo podem ser modificados no objeto JavaScript:

```javascript
let projects = [
    {
        id: 1,
        name: "Meu Projeto",
        // ... outros campos
    }
];
```

## 📱 Responsividade

O sistema é totalmente responsivo e funciona em:
- 💻 **Desktop** (1200px+)
- 📱 **Tablet** (768px - 1199px)
- 📱 **Mobile** (até 767px)

## 🐛 Solução de Problemas

### Problemas Comuns

1. **Login não funciona:**
   - Verifique se está usando `admin` / `123456`
   - Confirme que o JavaScript está habilitado

2. **Dados não aparecem:**
   - Recarregue a página
   - Verifique o console do navegador (F12)

3. **Layout quebrado:**
   - Limpe o cache do navegador
   - Verifique a conexão com CDNs

## 🔄 Versões

### v1.0.0 (Atual)
- ✅ Sistema completo de gestão
- ✅ Interface moderna
- ✅ Todas as funcionalidades básicas
- ✅ Dados em memória

### Próximas Versões
- [ ] Persistência em banco de dados
- [ ] Sistema de notificações
- [ ] API REST
- [ ] Modo offline
- [ ] Aplicativo móvel

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para detalhes.

## 👨‍💻 Desenvolvido por

**Kauan Hugo**    
- GitHub: [@kauanhugo](https://github.com/kauanhugo)

## 🙏 Agradecimentos

- Equipe de desenvolvimento
- Contribuidores do projeto
- Comunidade open source

---

**⭐ Se este projeto foi útil, deixe uma estrela no repositório!**

---

<div align="center">

**GestãoPro** - Transformando a gestão de projetos

[Reportar Bug](https://github.com/seu-usuario/gestaopro/issues) · [Solicitar Feature](https://github.com/seu-usuario/gestaopro/issues)

</div>
