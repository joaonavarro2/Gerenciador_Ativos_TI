<template>
  <q-page class="consertos-page">
    <ConsertosHeader v-model:filtros="filtros" @novo="abrirModalNovoReparo" />

    <ConsertosTable
      :consertos="consertosFiltrados"
      :possui-filtros="possuiFiltros"
      :filtros="filtros"
      @visualizar="abrirModalVisualizacao"
      @editar="abrirModalEdicao"
      @excluir="abrirModalExclusao"
    />

    <NovoReparo v-model="dialogNovoReparo" @salvar="adicionarReparo" />
    <EditarReparo v-model="dialogEditarReparo" :reparo="reparoSelecionado" @salvar="atualizarReparo" />
    <VisualizarReparo v-model="dialogVisualizarReparo" :reparo="reparoSelecionado" />
    <ExcluirReparo
      v-model="dialogExcluirReparo"
      :reparo="reparoSelecionado"
      @excluir="excluirReparo"
    />
  </q-page>
</template>

<script setup>
import { ref, computed } from 'vue'

import ConsertosHeader from '@/components/consertos/ConsertosHeader.vue'
import ConsertosTable from '@/components/consertos/ConsertosTable.vue'
import NovoReparo from '@/components/consertos/card/NovoReparo.vue'
import EditarReparo from '@/components/consertos/card/EditarReparo.vue'
import VisualizarReparo from '@/components/consertos/card/VisualizarReparo.vue'
import ExcluirReparo from '@/components/consertos/card/ExcluirReparo.vue'

const dialogNovoReparo = ref(false)
const dialogEditarReparo = ref(false)
const dialogVisualizarReparo = ref(false)
const dialogExcluirReparo = ref(false)
const reparoSelecionado = ref({})

const consertos = ref([
  { id: 1, ativoId: 'ATV-2001', problema: 'Quebra da tela', tipo: 'Reparo', status: 'Pendente', tecnico: 'Lucas', descricao: 'Tela trincada por queda', data: '2024-06-02', escritorio: 'Salvador', departamento: 'Diretoria Geral' },
  { id: 2, ativoId: 'ATV-2002', problema: 'Fonte com defeito', tipo: 'Substituição', status: 'Concluído', tecnico: 'Mariana', descricao: 'Substituição da fonte', data: '2024-06-15', escritorio: 'Feira de Santana', departamento: 'Dep. Financeiro' },
  { id: 3, ativoId: 'ATV-2003', problema: 'Teclado com teclas soltas', tipo: 'Reparo', status: 'Em andamento', tecnico: 'Paulo', descricao: 'Troca parcial do teclado', data: '2024-07-01', escritorio: 'Vitória da Conquista', departamento: 'Controle Interno' },
])

const filtros = ref({ busca: '', tipo: null, status: null, tecnico: null, departamento: null, escritorio: null, dataInicial: '', dataFinal: '' })

const possuiFiltros = computed(() => Object.values(filtros.value).some(Boolean))

function abrirModalNovoReparo() {
  dialogNovoReparo.value = true
}

function abrirModalEdicao(reparo) {
  reparoSelecionado.value = reparo
  dialogEditarReparo.value = true
}

function abrirModalVisualizacao(reparo) {
  reparoSelecionado.value = reparo
  dialogVisualizarReparo.value = true
}

function abrirModalExclusao(reparo) {
  reparoSelecionado.value = reparo
  dialogExcluirReparo.value = true
}

function excluirReparo(id) {
  consertos.value = consertos.value.filter(item => item.id !== id)
}

function adicionarReparo(novoReparo) {
  const novoItem = {
    id: Date.now(),
    ativoId: novoReparo.ativoId,
    problema: novoReparo.problema,
    tipo: novoReparo.tipo,
    status: novoReparo.status || 'Pendente',
    tecnico: novoReparo.tecnico,
    descricao: novoReparo.descricao || novoReparo.problema,
    data: novoReparo.data || novoReparo.dataInicio || new Date().toISOString().slice(0, 10),
    escritorio: novoReparo.escritorio || 'Não informado',
    departamento: novoReparo.departamento || 'Não informado',
    dataInicio: novoReparo.dataInicio,
    dataConclusao: novoReparo.dataConclusao,
  }

  consertos.value.unshift(novoItem)
}

function atualizarReparo(reparoAtualizado) {
  consertos.value = consertos.value.map(item =>
    item.id === reparoAtualizado.id
      ? {
          ...item,
          ...reparoAtualizado,
          data: reparoAtualizado.data || reparoAtualizado.dataInicio || item.data,
        }
      : item,
  )
}

function texto(valor) {
  if (valor === null || valor === undefined) return ''
  if (Array.isArray(valor)) return valor.map(v => texto(v)).join(', ')
  if (typeof valor === 'object') return valor.nome || valor.label || valor.name || valor.value || ''
  return String(valor)
}

function normalizar(valor) {
  return texto(valor).trim().toLowerCase()
}

const consertosFiltrados = computed(() => {
  const f = filtros.value
  return consertos.value.filter(item => {
    if (f.busca) {
      const b = normalizar(f.busca)
      const combinado = `${item.ativoId} ${item.problema} ${item.descricao} ${item.tecnico}`
      if (!normalizar(combinado).includes(b)) return false
    }

    if (f.status && String(f.status).trim() !== '') {
      if (normalizar(item.status) !== normalizar(f.status)) return false
    }

    if (f.tipo && String(f.tipo).trim() !== '') {
      if (normalizar(item.tipo) !== normalizar(f.tipo)) return false
    }

    if (f.departamento && String(f.departamento).trim() !== '') {
      if (normalizar(item.departamento) !== normalizar(f.departamento)) return false
    }

    if (f.escritorio && String(f.escritorio).trim() !== '') {
      if (normalizar(item.escritorio) !== normalizar(f.escritorio)) return false
    }

    if (f.tecnico && String(f.tecnico).trim() !== '') {
      if (normalizar(item.tecnico) !== normalizar(f.tecnico)) return false
    }

    if (f.dataInicial) {
      if (new Date(item.data) < new Date(f.dataInicial)) return false
    }
    if (f.dataFinal) {
      if (new Date(item.data) > new Date(f.dataFinal)) return false
    }

    return true
  })
})

</script>

<style scoped>
.consertos-page {
  padding: 16px;
}
</style>
