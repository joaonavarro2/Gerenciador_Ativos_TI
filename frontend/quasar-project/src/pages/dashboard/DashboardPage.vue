<template>
  <q-page class="dashboard-page">
    <DashboardHeader />

    <DashboardSummary :summary="dashboard.summary" />

    <DashboardCharts
      :monthly-activity="dashboard.monthlyActivity"
      :category-distribution="dashboard.categoryDistribution"
    />

    <DashboardAssetsTable :rows="dashboard.recentAssets" />
  </q-page>
</template>

<script setup>
import DashboardHeader from '@/components/dashboard/DashboardHeader.vue'
import DashboardSummary from '@/components/dashboard/DashboardSummary.vue'
import DashboardCharts from '@/components/dashboard/DashboardCharts.vue'
import DashboardAssetsTable from '@/components/dashboard/DashboardAssetsTable.vue'

const dashboard = {
  summary: {
    totalAssets: 148,
    pendingMovements: 18,
    maintenanceOpen: 6,
    itemsInAnalysis: 11,
    totalAssetsDelta: '+8.4%',
    pendingMovementsDelta: '-3.1%',
    maintenanceOpenDelta: '+1.2%',
    itemsInAnalysisDelta: '+2.7%',
  },
  monthlyActivity: [
    { month: 'Jan', movimentacoes: 18, consertos: 7 },
    { month: 'Fev', movimentacoes: 22, consertos: 9 },
    { month: 'Mar', movimentacoes: 26, consertos: 12 },
    { month: 'Abr', movimentacoes: 24, consertos: 10 },
    { month: 'Mai', movimentacoes: 29, consertos: 13 },
    { month: 'Jun', movimentacoes: 31, consertos: 14 },
  ],
  categoryDistribution: [
    { name: 'Computadores', value: 42 },
    { name: 'Periféricos', value: 23 },
    { name: 'Rede', value: 18 },
    { name: 'Impressão', value: 10 },
    { name: 'Outros', value: 7 },
  ],
  recentAssets: [
    { id: 'BEM-001', descricao: 'Notebook Dell Latitude 5440', unidade: 'Salvador', departamento: 'Diretoria', data: '2026-08-21', status: 'Disponível' },
    { id: 'BEM-018', descricao: 'Impressora HP LaserJet Pro', unidade: 'Vitória da Conquista', departamento: 'Financeiro', data: '2026-08-18', status: 'Em manutenção' },
    { id: 'BEM-034', descricao: 'Switch Cisco 48P', unidade: 'Barreiras', departamento: 'TI', data: '2026-08-16', status: 'Em uso' },
    { id: 'BEM-061', descricao: 'Monitor LG 24"', unidade: 'Alagoinhas', departamento: 'Controle Interno', data: '2026-08-14', status: 'Disponível' },
    { id: 'BEM-095', descricao: 'Servidor de Rede', unidade: 'Amargosa', departamento: 'Infraestrutura', data: '2026-08-12', status: 'Em análise' },
  ],
}
</script>