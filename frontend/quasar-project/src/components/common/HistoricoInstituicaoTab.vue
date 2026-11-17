<template>
  <div class="q-pa-md">
    <div class="text-subtitle1 text-weight-bold q-mb-md">Histórico da instituição</div>
    <q-inner-loading :showing="carregando"><q-spinner size="32px" color="primary" /></q-inner-loading>

    <q-banner v-if="erro" dense rounded class="bg-red-1 text-negative">
      {{ erro }}
    </q-banner>

    <q-list v-else-if="historico.length" bordered separator>
      <q-item v-for="registro in historico" :key="registro.id">
        <q-item-section avatar><q-icon name="account_balance" color="primary" /></q-item-section>
        <q-item-section>
          <q-item-label>
            {{ registro.instituicaoAnterior || 'Sem instituição' }}
            <q-icon name="arrow_forward" size="14px" />
            {{ registro.instituicaoNova }}
          </q-item-label>
          <q-item-label caption>
            {{ formatarData(registro.alteradoEm) }} · {{ registro.usuario || 'Usuário não identificado' }}
          </q-item-label>
        </q-item-section>
      </q-item>
    </q-list>

    <div v-else-if="!carregando" class="text-grey-7">Nenhuma alteração de instituição registrada.</div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { api } from '@/boot/axios'

const props = defineProps({
  entidade: { type: String, required: true },
  entidadeId: { type: [Number, String], default: null },
})

const historico = ref([])
const carregando = ref(false)
const erro = ref('')
let requisicao = 0

watch(
  () => [props.entidade, props.entidadeId],
  async ([entidade, id]) => {
    const atual = ++requisicao
    historico.value = []
    erro.value = ''
    if (!id) return

    carregando.value = true
    try {
      const { data } = await api.get(`/${entidade}/${id}/historico-instituicao`)
      if (atual === requisicao) historico.value = data
    } catch (error) {
      if (atual !== requisicao) return
      erro.value = error.response?.data?.message || 'Não foi possível carregar o histórico da instituição.'
    } finally {
      if (atual === requisicao) carregando.value = false
    }
  },
  { immediate: true },
)

function formatarData(valor) {
  if (!valor) return 'Data não disponível'
  return new Date(valor).toLocaleString('pt-BR')
}
</script>
