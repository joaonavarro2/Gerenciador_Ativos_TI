<template>
  <q-dialog :model-value="modelValue" persistent transition-show="fade" transition-hide="fade" @update:model-value="emit('update:modelValue', $event)">
    <q-card class="outro-item-dialog-card">
      <q-card-section class="row items-center justify-between q-pb-sm">
        <div class="row items-center">
          <q-avatar color="green-1" text-color="teal" icon="visibility" size="48px" />
          <div class="q-ml-md">
            <div class="text-h5 text-weight-bold">Detalhes do Item</div>
            <div class="text-subtitle2 text-grey-7">Informações do item de estoque selecionado.</div>
          </div>
        </div>
        <q-btn flat round dense icon="close" @click="fechar" />
      </q-card-section>
      <q-separator />

      <q-card-section class="outro-item-dialog-scroll">
        <div class="titulo-secao">IDENTIFICAÇÃO DO ITEM</div>
        <div class="info-grid">
          <div class="info-item"><span>CÓDIGO</span><strong>{{ item.codigo || '-' }}</strong></div>
          <div class="info-item"><span>NOME DO ITEM</span><strong>{{ item.nome || '-' }}</strong></div>
          <div class="info-item"><span>CATEGORIA</span><strong>{{ item.categoria || '-' }}</strong></div>
          <div class="info-item"><span>FORNECEDOR</span><strong>{{ item.fornecedor || '-' }}</strong></div>
        </div>

        <div class="titulo-secao q-mt-xl">LOCALIZAÇÃO E ESTOQUE</div>
        <div class="info-grid">
          <div class="info-item"><span>ESCRITÓRIO</span><strong>{{ item.escritorio || '-' }}</strong></div>
          <div class="info-item"><span>LOCALIZAÇÃO</span><strong>{{ item.localizacao || '-' }}</strong></div>
          <div class="info-item"><span>QTD. DISPONÍVEL</span><strong>{{ item.quantidade ?? '-' }}</strong></div>
          <div class="info-item"><span>EST. MÍNIMO</span><strong>{{ item.estoqueMinimo ?? '-' }}</strong></div>
          <div class="info-item"><span>STATUS</span><q-chip dense :class="statusClass(item.status)">{{ item.status || '-' }}</q-chip></div>
        </div>
      </q-card-section>

      <q-card-actions align="right" class="q-pa-md outro-item-dialog-actions">
        <q-btn flat no-caps label="Fechar" @click="fechar" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
defineProps({ modelValue: Boolean, item: { type: Object, default: () => ({}) } })
const emit = defineEmits(['update:modelValue'])
function fechar() { emit('update:modelValue', false) }
function statusClass(status) {
  if (status === 'Ativo') return 'status-chip status-ativo'
  if (status === 'Inativo') return 'status-chip status-inativo'
  return 'status-chip status-default'
}
</script>

<style scoped>
.outro-item-dialog-card { width: min(760px, 92vw); max-width: 92vw; max-height: 90vh; border-radius: 18px; overflow: hidden; display: flex; flex-direction: column; }
.outro-item-dialog-scroll { overflow-y: auto; flex: 1 1 auto; }
.outro-item-dialog-actions { border-top: 1px solid #e5e7eb; background: #fff; }
.titulo-secao { margin-bottom: 16px; color: #1f2937; font-size: 13px; font-weight: 700; letter-spacing: 0.06em; }
.info-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 18px 24px; }
.info-item { display: flex; flex-direction: column; gap: 6px; min-height: 58px; }
.info-item span { color: #6b7280; font-size: 11px; letter-spacing: 0.08em; font-weight: 700; }
.info-item strong { color: #111827; font-size: 15px; font-weight: 500; }
@media (max-width: 600px) { .info-grid { grid-template-columns: 1fr; } }
</style>
