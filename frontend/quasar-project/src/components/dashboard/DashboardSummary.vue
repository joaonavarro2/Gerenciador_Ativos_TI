<template>
  <div class="dashboard-summary">
    <q-card class="summary-card">
      <div class="summary-card-top">
        <div>
          <div class="summary-title">Total de Bens</div>
          <div class="summary-value">{{ summary.totalAssets }}</div>
        </div>

        <div class="summary-icon bg-success-light">
          <q-icon name="inventory_2" class="icon-success" />
        </div>
      </div>
    </q-card>

    <q-card class="summary-card">
      <div class="summary-card-top">
        <div>
          <div class="summary-title">Movimentações Pendentes</div>
          <div class="summary-value">{{ summary.pendingMovements }}</div>
        </div>

        <div class="summary-icon bg-warning-light">
          <q-icon name="sync_alt" class="icon-warning" />
        </div>
      </div>
    </q-card>

    <q-card class="summary-card">
      <div class="summary-card-top">
        <div>
          <div class="summary-title">Consertos Abertos</div>
          <div class="summary-value">{{ summary.maintenanceOpen }}</div>
        </div>

        <div class="summary-icon bg-danger-light">
          <q-icon name="build" class="icon-danger" />
        </div>
      </div>
    </q-card>

    <q-card class="summary-card">
      <div class="summary-card-top">
        <div>
          <div class="summary-title">Itens em Análise</div>
          <div class="summary-value">{{ summary.itemsInAnalysis }}</div>
        </div>

        <div class="summary-icon bg-info-light">
          <q-icon name="pending_actions" class="icon-info" />
        </div>
      </div>
    </q-card>
  </div>
</template>

<script setup>
defineProps({
  summary: {
    type: Object,
    default: () => ({
      totalAssets: 0,
      pendingMovements: 0,
      maintenanceOpen: 0,
      itemsInAnalysis: 0,
    }),
  },
})
</script>
