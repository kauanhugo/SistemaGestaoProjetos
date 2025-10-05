// Variáveis globais para a modal
let teamMembers = [];
let selectedTeamMembers = [];

// Inicializar modal de projeto
function initProjectModal() {
    const modal = document.getElementById('projectModal');
    const closeBtn = document.querySelector('.close-modal');
    const cancelBtn = document.querySelector('.btn-cancel');
    const fab = document.getElementById('createProjectFab');
    const form = document.getElementById('advancedProjectForm');
    
    // Abrir modal
    if (fab) {
        fab.addEventListener('click', openProjectModal);
    }
    
    // Fechar modal
    if (closeBtn) {
        closeBtn.addEventListener('click', closeProjectModal);
    }
    
    if (cancelBtn) {
        cancelBtn.addEventListener('click', closeProjectModal);
    }
    
    // Fechar ao clicar fora
    if (modal) {
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                closeProjectModal();
            }
        });
    }
    
    // Submeter formulário
    if (form) {
        form.addEventListener('submit', handleAdvancedProjectCreate);
    }
    
    // Inicializar seleção de prioridade
    initPrioritySelection();
    
    // Configurar datas padrão
    setDefaultDates();
}

// Abrir modal de projeto
function openProjectModal() {
    const modal = document.getElementById('projectModal');
    if (!modal) return;
    
    // Resetar formulário
    resetProjectForm();
    
    // Carregar membros da equipe
    loadTeamMembers();
    
    // Mostrar modal
    modal.style.display = 'block';
    document.body.style.overflow = 'hidden';
}

// Fechar modal de projeto
function closeProjectModal() {
    const modal = document.getElementById('projectModal');
    if (!modal) return;
    
    modal.style.display = 'none';
    document.body.style.overflow = 'auto';
}

// Resetar formulário
function resetProjectForm() {
    const form = document.getElementById('advancedProjectForm');
    if (form) {
        form.reset();
    }
    
    selectedTeamMembers = [];
    setDefaultDates();
    initPrioritySelection();
    
    // Resetar seleção de equipe visualmente
    const teamMembers = document.querySelectorAll('.team-member');
    teamMembers.forEach(member => {
        member.classList.remove('selected');
    });
}

// Configurar datas padrão
function setDefaultDates() {
    const startDate = document.getElementById('projectStart');
    const deadline = document.getElementById('projectDeadline');
    
    if (startDate) {
        const today = new Date().toISOString().split('T')[0];
        startDate.value = today;
    }
    
    if (deadline) {
        const nextMonth = new Date();
        nextMonth.setMonth(nextMonth.getMonth() + 1);
        const nextMonthStr = nextMonth.toISOString().split('T')[0];
        deadline.value = nextMonthStr;
    }
}

// Inicializar seleção de prioridade
function initPrioritySelection() {
    const priorityOptions = document.querySelectorAll('.priority-option');
    const priorityInput = document.getElementById('projectPriority');
    
    // Selecionar média por padrão
    const defaultOption = document.querySelector('.priority-option[data-priority="MEDIA"]');
    if (defaultOption) {
        defaultOption.classList.add('selected');
    }
    
    priorityOptions.forEach(option => {
        option.addEventListener('click', function() {
            // Remover seleção anterior
            priorityOptions.forEach(opt => opt.classList.remove('selected'));
            
            // Adicionar seleção atual
            this.classList.add('selected');
            
            // Atualizar input hidden
            if (priorityInput) {
                priorityInput.value = this.getAttribute('data-priority');
            }
        });
    });
}

// Carregar membros da equipe
async function loadTeamMembers() {
    try {
        const response = await apiCall('/usuarios', 'GET');
        teamMembers = response.usuarios || [];
        renderTeamSelection();
    } catch (error) {
        console.error('Erro ao carregar membros:', error);
        teamMembers = [];
    }
}

// Renderizar seleção de equipe
function renderTeamSelection() {
    const container = document.getElementById('teamSelection');
    if (!container) return;
    
    container.innerHTML = teamMembers.map(member => `
        <div class="team-member" data-user-id="${member.id}" data-user-login="${member.login}">
            <div class="member-avatar">
                ${getUserInitials(member.nome)}
            </div>
            <div class="member-info">
                <div class="member-name">${member.nome}</div>
                <div class="member-role">${formatPerfil(member.perfil)}</div>
            </div>
        </div>
    `).join('');
    
    // Adicionar event listeners para seleção
    const teamMemberElements = document.querySelectorAll('.team-member');
    teamMemberElements.forEach(element => {
        element.addEventListener('click', function() {
            this.classList.toggle('selected');
            
            const userId = this.getAttribute('data-user-id');
            const userLogin = this.getAttribute('data-user-login');
            
            if (this.classList.contains('selected')) {
                // Adicionar à seleção
                if (!selectedTeamMembers.find(m => m.id == userId)) {
                    selectedTeamMembers.push({
                        id: userId,
                        login: userLogin,
                        nome: this.querySelector('.member-name').textContent
                    });
                }
            } else {
                // Remover da seleção
                selectedTeamMembers = selectedTeamMembers.filter(m => m.id != userId);
            }
        });
    });
}

// Obter iniciais do usuário
function getUserInitials(name) {
    if (!name) return 'U';
    return name.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2);
}

// Manipular criação avançada de projeto
async function handleAdvancedProjectCreate(event) {
    event.preventDefault();
    
    const form = event.target;
    const submitBtn = form.querySelector('.btn-submit');
    const submitText = submitBtn.querySelector('.submit-text');
    const loadingSpinner = submitBtn.querySelector('.loading-spinner');
    
    // Obter dados do formulário
    const formData = new FormData(form);
    const projectData = {
        nome: formData.get('projectName'),
        descricao: formData.get('projectDescription'),
        prioridade: formData.get('projectPriority'),
        dataInicio: formData.get('projectStart'),
        prazoFinal: formData.get('projectDeadline'),
        orcamento: formData.get('projectBudget'),
        categoria: formData.get('projectCategory'),
        equipe: selectedTeamMembers.map(m => m.login)
    };
    
    // Validação básica
    if (!projectData.nome) {
        showNotification('Por favor, insira um nome para o projeto', 'error');
        return;
    }
    
    try {
        // Mostrar loading
        submitText.style.display = 'none';
        loadingSpinner.style.display = 'inline';
        submitBtn.disabled = true;
        
        // Criar projeto via API
        const response = await apiCall('/projetos', 'POST', {
            nome: projectData.nome,
            descricao: projectData.descricao
        });
        
        if (response.success) {
            showNotification('Projeto criado com sucesso!', 'success');
            closeProjectModal();
            
            // Adicionar membros da equipe se houver seleção
            if (selectedTeamMembers.length > 0) {
                await addTeamMembersToProject(projectData.nome, selectedTeamMembers);
            }
            
            // Recarregar dados
            loadDashboard();
            loadProjetos();
        } else {
            showNotification('Erro ao criar projeto: ' + response.message, 'error');
        }
        
    } catch (error) {
        showNotification('Erro ao criar projeto: ' + error.message, 'error');
    } finally {
        // Restaurar botão
        submitText.style.display = 'inline';
        loadingSpinner.style.display = 'none';
        submitBtn.disabled = false;
    }
}

// Adicionar membros ao projeto
async function addTeamMembersToProject(projectName, members) {
    try {
        // Primeiro precisamos obter a lista de projetos para encontrar o ID
        const projetosResponse = await apiCall('/projetos', 'GET');
        const projeto = projetosResponse.projetos.find(p => p.nome === projectName);
        
        if (projeto) {
            // Adicionar cada membro selecionado
            for (const member of members) {
                if (member.login !== currentUser.login) { // Não adicionar o usuário atual novamente
                    await apiCall(`/projetos/${projeto.id}/membros`, 'POST', {
                        loginMembro: member.login
                    });
                }
            }
        }
    } catch (error) {
        console.error('Erro ao adicionar membros:', error);
    }
}

// Atualizar a função showSection para incluir a modal
function showSection(sectionId) {
    // Esconder todas as seções
    document.querySelectorAll('.section').forEach(section => {
        section.classList.remove('active');
    });
    
    // Remover active de todos os botões
    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    
    // Mostrar seção selecionada
    document.getElementById(sectionId).classList.add('active');
    
    // Ativar botão correspondente
    if (event && event.target) {
        event.target.classList.add('active');
    }
    
    // Carregar dados específicos da seção
    switch(sectionId) {
        case 'dashboard':
            loadDashboard();
            break;
        case 'projetos':
            loadProjetos();
            break;
        case 'usuarios':
            loadUsuarios();
            break;
    }
}

// Adicionar clique nos cards de projeto (para futuras expansões)
function setupProjectCardClicks() {
    document.addEventListener('click', function(e) {
        const projectCard = e.target.closest('.project-card');
        if (projectCard && !e.target.closest('.project-actions')) {
            // Aqui você pode expandir para mostrar detalhes do projeto
            const projectName = projectCard.querySelector('.project-name').textContent;
            showNotification(`Clicou no projeto: ${projectName}`, 'info');
        }
    });
}

// Inicializar tudo quando o DOM carregar
document.addEventListener('DOMContentLoaded', function() {
    checkAuthStatus();
    setupEventListeners();
    initProjectModal();
    setupProjectCardClicks();
});