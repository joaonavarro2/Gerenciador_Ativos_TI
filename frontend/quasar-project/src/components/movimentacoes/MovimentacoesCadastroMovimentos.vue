<template>
  <q-dialog v-model="aberto" persistent>
    <q-card class="cadastro-movimentacao-dialog">
      <q-card-section class="cadastro-movimentacao-header">
        <div>
          <div class="cadastro-movimentacao-title">Registrar Movimentação</div>
          <div class="cadastro-movimentacao-subtitle">Selecione o bem e informe o destino.</div>
        </div>
        <q-btn v-close-popup flat round dense icon="close" />
      </q-card-section>
      <q-form @submit.prevent="registrar">
        <q-card-section class="cadastro-movimentacao-content">
          <div class="cadastro-movimentacao-grid">
            <q-select v-model="itemId" outlined dense class="cadastro-movimentacao-field cadastro-movimentacao-field-full"
              label="Bem *" :options="itens" option-label="rotulo" option-value="id" emit-value map-options
              use-input input-debounce="250" @filter="filtrarItens" :loading="carregandoOpcoes" :rules="[obrigatorio]" />
            <q-input :model-value="itemSelecionado?.nome || ''" outlined dense readonly label="Nome do bem" class="cadastro-movimentacao-field" />
            <q-select v-model="form.tipo" outlined dense label="Tipo *" :options="tipos" class="cadastro-movimentacao-field" :rules="[obrigatorio]" />
            <q-input v-model="form.data" outlined dense type="datetime-local" label="Data e hora *" class="cadastro-movimentacao-field" :rules="[obrigatorio]" />
            <q-select v-model="form.escritorioDestinoId" outlined dense label="Escritório de destino *" :options="opcoes.escritorios" option-label="nome" option-value="id" emit-value map-options class="cadastro-movimentacao-field" :rules="[obrigatorio]" />
            <q-select v-model="form.departamentoDestinoId" outlined dense label="Departamento de destino *" :options="opcoes.departamentos" option-label="nome" option-value="id" emit-value map-options class="cadastro-movimentacao-field" :rules="[obrigatorio]" />
            <q-select v-model="form.responsavelDestinoId" outlined dense clearable label="Responsável pelo recebimento" :options="pessoasDestino" option-label="nome" option-value="id" emit-value map-options class="cadastro-movimentacao-field cadastro-movimentacao-field-full" />
            <q-input v-model="form.justificativa" outlined type="textarea" maxlength="2000" label="Justificativa / motivo" class="cadastro-movimentacao-field cadastro-movimentacao-field-full" />
          </div>
          <q-banner v-if="erro" dense rounded class="bg-red-1 text-negative q-mt-md">{{ erro }}</q-banner>
        </q-card-section>
        <q-card-actions align="right" class="cadastro-movimentacao-footer">
          <q-btn v-close-popup outline no-caps label="Cancelar" />
          <q-btn type="submit" unelevated no-caps icon="check_circle_outline" label="Registrar movimentação" :loading="salvando" :disable="!formValido" />
        </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { api } from '@/boot/axios.js'

const aberto = defineModel({ type: Boolean, default: false })
const emit = defineEmits(['registrar'])
const tipos = ['Transferência', 'Empréstimo', 'Devolução']
const itens = ref([])
const todosItens = ref([])
const itemId = ref(null)
const opcoes = ref({ escritorios: [], departamentos: [], pessoas: [] })
const salvando = ref(false)
const carregandoOpcoes = ref(false)
const erro = ref('')
const form = ref({ tipo: null, data: new Date(Date.now() - new Date().getTimezoneOffset() * 60000).toISOString().slice(0, 16), escritorioDestinoId: null, departamentoDestinoId: null, responsavelDestinoId: null, justificativa: '' })
const itemSelecionado = computed(() => todosItens.value.find((item) => item.id === itemId.value))
const pessoasDestino = computed(() => opcoes.value.pessoas.filter((pessoa) => !form.value.departamentoDestinoId || pessoa.departamentoId === form.value.departamentoDestinoId))
const obrigatorio = (valor) => valor !== null && valor !== undefined && valor !== '' || 'Campo obrigatório'
const formValido = computed(() => itemId.value && form.value.tipo && form.value.data && form.value.escritorioDestinoId && form.value.departamentoDestinoId)

watch(aberto, async (visivel) => {
  if (!visivel) return
  erro.value = ''
  carregandoOpcoes.value = true
  try {
    const [lista, catalogo] = await Promise.all([api.get('/bens'), api.get('/bens/opcoes')])
    todosItens.value = lista.data
    itens.value = lista.data.map((item) => ({ ...item, rotulo: `${item.codigo} · ${item.nome}` }))
    opcoes.value = catalogo.data
  } catch (e) {
    erro.value = e.response?.data?.mensagem || 'Não foi possível carregar bens e opções.'
  } finally { carregandoOpcoes.value = false }
})

watch(itemSelecionado, (item) => {
  if (!item) return
  form.value.escritorioDestinoId = item.escritorioId
  form.value.departamentoDestinoId = item.departamentoId
  form.value.responsavelDestinoId = item.pessoaId
})

function filtrarItens(valor, atualizar) {
  atualizar(() => {
    const termo = valor?.toLocaleLowerCase('pt-BR') || ''
    itens.value = todosItens.value.map((item) => ({ ...item, rotulo: `${item.codigo} · ${item.nome}` }))
      .filter((item) => `${item.codigo} ${item.nome} ${item.patrimonio || ''}`.toLocaleLowerCase('pt-BR').includes(termo))
  })
}

async function registrar() {
  if (!formValido.value || !itemSelecionado.value) return
  salvando.value = true
  erro.value = ''
  try {
    const resposta = await api.post(`/bens/${itemId.value}/movimentacoes`, {
      tipo: form.value.tipo,
      data: `${form.value.data}:00`,
      escritorioDestinoId: form.value.escritorioDestinoId,
      departamentoDestinoId: form.value.departamentoDestinoId,
      responsavelDestinoId: form.value.responsavelDestinoId,
      justificativa: form.value.justificativa || null,
    })
    emit('registrar', resposta.data)
    aberto.value = false
    itemId.value = null
    form.value.justificativa = ''
  } catch (e) {
    erro.value = e.response?.data?.mensagem || 'Não foi possível registrar a movimentação.'
  } finally { salvando.value = false }
}
</script>
