const routes = [
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
    {
      path:'',
      component:() => import('@/pages/dashboard/DashboardPage.vue')
    }, 
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

    // {
    //   path:'historicos',
    //   component:() => import('@/pages/historicos/HistoricosPage.vue')
    // },

    // {
    //   path:'consertos',
    //   component:() => import('@/pages/consertos/ConsertosPage.vue')
    // },

    // {
    //   path:'escritorios',
    //   component:() => import('@/pages/escritorios/EscritoriosPage.vue')
    // },

    // {
    //   path:'departamentos',
    //   component:() => import('@/pages/departamentos/DepartamentosPage.vue')
    // },

    // {
    //   path:'pessoas',
    //   component:() => import('@/pages/pessoas/PessoasPage.vue')
    // },

    // {
    //   path:'outros-itens',
    //   component:() => import('@/pages/outros-itens/OutrosItensPage.vue')
    // }
    ]
  },

  {
    path: '/:catchAll(.*)*',
    component: () => import('@/pages/ErrorNotFound.vue')
  }
]

export default routes