<template>
  <BaseDialog
    :model-value="modelValue"
    title="Excluir Item"
    subtitle="Esta ação remove o item do estoque."
    icon="delete"
    icon-color="negative"
    icon-background="red-1"
    width="640px"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <q-card-section class="q-pa-lg">
      <div class="warning-box">
        <q-icon name="warning" size="34px" color="warning" />
        <div>
          <div class="warning-title">Tem certeza que deseja excluir este item?</div>
          <div class="warning-text">Depois da confirmação, o registro será removido permanentemente.</div>
        </div>
      </div>

      <q-card flat bordered class="item-resumo q-mt-md">
        <div class="resumo-item"><span>Código</span><strong>{{ item.codigo || '--' }}</strong></div>
        <div class="resumo-item"><span>Item</span><strong>{{ item.nome || '--' }}</strong></div>
        <div class="resumo-item"><span>Categoria</span><strong>{{ item.categoria || '--' }}</strong></div>
        <div class="resumo-item"><span>Escritório</span><strong>{{ item.escritorio || '--' }}</strong></div>
      </q-card>
    </q-card-section>

    <template #footer>
      <q-card-actions align="right" class="q-pa-md">
        <q-btn flat no-caps label="Cancelar" @click="fechar" />
        <q-btn color="negative" no-caps icon="delete" label="Excluir Item" :disable="!item.id" @click="confirmar" />
      </q-card-actions>
    </template>
  </BaseDialog>
</template>

<script setup>
import BaseDialog from '@/components/common/BaseDialog.vue'

const props = defineProps({ modelValue: Boolean, item: { type: Object, default: () => ({}) } })
const emit = defineEmits(['update:modelValue', 'excluir'])
function fechar() { emit('update:modelValue', false) }
function confirmar() { if (!props.item.id) return; emit('excluir', props.item.id); fechar() }
</script>

<style scoped>
.warning-box { display: flex; align-items: flex-start; gap: 16px; padding: 18px 20px; border-radius: 14px; background: #fff7ed; border: 1px solid #fed7aa; }
.warning-title { font-size: 1.05rem; font-weight: 700; color: #1f2937; }
.warning-text { margin-top: 4px; color: #475467; font-size: 0.92rem; }
.item-resumo { border-radius: 14px; padding: 12px 14px; }
.resumo-item { display: flex; justify-content: space-between; align-items: center; gap: 20px; padding: 10px 0; border-bottom: 1px solid #f2f4f7; }
.resumo-item:last-child { border-bottom: none; }
.resumo-item span { color: #667085; font-size: 0.78rem; text-transform: uppercase; letter-spacing: 0.05em; }
.resumo-item strong { text-align: right; }
</style>
