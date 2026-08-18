<template>
  <q-card flat bordered class="historico-table-card">
    <div class="historico-table-header">
      <div class="historico-table-title">
        <q-icon name="history" size="18px" />
        <span>Registros de Histórico</span>
      </div>

      <div class="historico-filtro-status">
        <q-icon name="filter_alt" size="16px" />
        {{ possuiFiltros ? 'Filtros aplicados' : 'Sem filtros' }}
      </div>
    </div>

    <div v-if="!historicos.length" class="historico-empty-state">
      <div class="historico-empty-icon"><q-icon name="history" size="34px" /></div>
      <h2>Nenhum registro de histórico encontrado.</h2>
      <p>
        O histórico de ativos aparecerá aqui após os ativos começarem a<br class="gt-xs" />
        receber movimentações, manutenções, atribuições e mudanças de status.
      </p>
    </div>
  </q-card>
</template>

<script setup>
defineProps({
  historicos: { type: Array, default: () => [] },
  possuiFiltros: { type: Boolean, default: false },
})
</script>
