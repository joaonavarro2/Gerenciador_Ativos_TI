const routes = [
  {
    path: '/',
    component: () => import('@/pages/login/LoginPage.vue')
  },
  {
    path: '/login',
    component: () => import('@/pages/login/LoginPage.vue')
  },
  {
    path: '/recuperar-senha',
    component: () => import('@/pages/login/RecuperarSenhaPage.vue')
  },
  {
    path: '/cadastro-usuario',
    component: () => import('@/pages/login/CadastroUsuarioPage.vue')
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
    {
      path:'dashboard',
      component:() => import('@/pages/dashboard/DashboardPage.vue')
    },

    {
      path:'bens',
      component:() => import('@/pages/bens/BensPage.vue')
    },

    {
      path:'movimentacoes',
      component:() => import('@/pages/movimentacoes/MovimentacoesPage.vue')
    },

    {
      path:'historicos',
      component:() => import('@/pages/historico/HistoricoPage.vue')
    },

    {
      path:'consertos',
      component:() => import('@/pages/consertos/ConsertosPage.vue')
    },

    {
      path:'escritorios',
      component:() => import('@/pages/escritorios/EscritoriosPage.vue')
    },
    {
      path:'departamentos',
      component:() => import('@/pages/departamentos/DepartamentosPages.vue')
    },

    {
      path:'pessoas',
      component:() => import('@/pages/pessoas/PessoasPage.vue')
    },

    {
      path:'outros-itens',
      component:() => import('@/pages/outrosItens/OutrosItensPagge.vue')
    }
    ]
  },

  {
    path: '/:catchAll(.*)*',
    component: () => import('@/pages/ErrorNotFound.vue')
  }
]

export default routes
