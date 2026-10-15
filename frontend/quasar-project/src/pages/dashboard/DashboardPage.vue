<template>
  <q-page class="dashboard-page">
    <DashboardHeader />

    <div v-if="loading" class="row justify-center q-pa-xl">
      <q-spinner color="primary" size="40px" />
    </div>

    <q-banner v-else-if="error" class="bg-negative text-white q-mb-md" rounded>
      {{ error }}
      <template #action>
        <q-btn flat color="white" label="Tentar novamente" @click="carregarDashboard" />
      </template>
    </q-banner>

    <template v-else>
      <DashboardSummary :summary="dashboard.summary" />

      <DashboardCharts
        :monthly-activity="dashboard.monthlyActivity"
        :category-distribution="dashboard.categoryDistribution"
        :year="dashboard.year"
      />

      <DashboardAssetsTable :rows="dashboard.recentAssets" />
    </template>
  </q-page>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { api } from '@/boot/axios'
import DashboardHeader from '@/components/dashboard/DashboardHeader.vue'
import DashboardSummary from '@/components/dashboard/DashboardSummary.vue'
import DashboardCharts from '@/components/dashboard/DashboardCharts.vue'
import DashboardAssetsTable from '@/components/dashboard/DashboardAssetsTable.vue'

const dashboard = reactive({
  summary: {},
  monthlyActivity: [],
  categoryDistribution: [],
  recentAssets: [],
  year: new Date().getFullYear(),
})
const loading = ref(true)
const error = ref('')
let requestId = 0

function mensagemErroDashboard(error) {
  if (!error.response) return 'Servidor indisponível. Confira a conexão e tente novamente.'
  if (error.response.status === 403) return 'Sua conta não tem permissão para ver o dashboard.'
  if (error.response.status >= 500)
    return 'O servidor encontrou uma falha temporária ao carregar o dashboard.'
  return error.response.data?.mensagem || 'Não foi possível carregar os dados do dashboard.'
}

async function carregarDashboard() {
  const chamadaAtual = ++requestId
  loading.value = true
  error.value = ''

  try {
    const { data } = await api.get('/dashboard')
    if (chamadaAtual !== requestId) return
    dashboard.summary = data.resumo
    dashboard.monthlyActivity = data.atividadeMensal || []
    dashboard.categoryDistribution = data.distribuicaoCategoria || []
    dashboard.recentAssets = data.bens || []
    dashboard.year = data.ano || new Date().getFullYear()
  } catch (err) {
    if (chamadaAtual !== requestId || err.code === 'ERR_CANCELED') return
    error.value = mensagemErroDashboard(err)
  } finally {
    if (chamadaAtual === requestId) loading.value = false
  }
}

onMounted(carregarDashboard)
</script>
