<template>
  <div class="dashboard-charts">
    <q-card class="dashboard-chart-card">
      <div class="dashboard-chart-header">
        <div>
          <h3 class="dashboard-chart-title">Atividade Mensal</h3>
          <p class="dashboard-chart-subtitle">Movimentações e consertos por mês — 2026</p>
        </div>
      </div>

      <div class="dashboard-chart-body">
        <div class="monthly-bars">
          <div v-for="item in monthlyActivity" :key="item.month" class="bar-group">
            <div class="bar-label">{{ item.month }}</div>
            <div class="bar-stack">
              <div class="bar bar-movimentacoes" :style="{ height: `${(item.movimentacoes / maxMonthlyValue) * 100}%` }" />
              <div class="bar bar-consertos" :style="{ height: `${(item.consertos / maxMonthlyValue) * 100}%` }" />
            </div>
            <div class="bar-values">
              <span>{{ item.movimentacoes }}</span>
              <span>{{ item.consertos }}</span>
            </div>
          </div>
        </div>
      </div>
    </q-card>

    <q-card class="dashboard-chart-card">
      <div class="dashboard-chart-header">
        <div>
          <h3 class="dashboard-chart-title">Distribuição por Categoria</h3>
          <p class="dashboard-chart-subtitle">Composição do patrimônio ativo</p>
        </div>
      </div>

      <div class="dashboard-chart-body">
        <div class="category-list">
          <div v-for="item in categoryDistribution" :key="item.name" class="category-row">
            <div class="category-meta">
              <span class="category-dot" :style="{ background: itemColor(item.name) }" />
              <span>{{ item.name }}</span>
            </div>
            <div class="category-stat">
              <div class="category-progress">
                <div :style="{ width: `${(item.value / maxCategoryValue) * 100}%`, background: itemColor(item.name) }" />
              </div>
              <span>{{ item.value }}%</span>
            </div>
          </div>
        </div>
      </div>
    </q-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  monthlyActivity: {
    type: Array,
    default: () => [],
  },
  categoryDistribution: {
    type: Array,
    default: () => [],
  },
})

const maxMonthlyValue = computed(() => {
  const values = props.monthlyActivity.flatMap(item => [item.movimentacoes, item.consertos])
  return values.length ? Math.max(...values) : 1
})

const maxCategoryValue = computed(() => {
  const values = props.categoryDistribution.map(item => item.value)
  return values.length ? Math.max(...values) : 1
})

function itemColor(name) {
  const palette = ['#4caf50', '#f4b740', '#3b82f6', '#ef4444', '#8b5cf6']
  const index = ['Computadores', 'Periféricos', 'Rede', 'Impressão', 'Outros'].indexOf(name)
  return palette[index >= 0 ? index : 0]
}
</script>