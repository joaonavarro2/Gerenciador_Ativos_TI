<template>
  <q-card flat bordered class="historico-table-card">
    <div class="historico-table-header">
      <div class="historico-table-title">
        <q-icon name="history" size="18px" />
        <span>Registros de Histórico</span>
      </div>

      <div class="historico-filtro-status">
          <q-icon name="filter_alt" size="16px" />
          <template v-if="possuiFiltros">
            <span v-for="(f, idx) in appliedFilters" :key="idx" class="historico-filtro-aplicado">
              <strong>{{ f.label }}:</strong> {{ f.value }}<span v-if="idx < appliedFilters.length - 1"> • </span>
            </span>
          </template>
          <template v-else>
            Sem filtros
          </template>
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
    
      <div v-else>
        <!-- Estética similar ao Histórico de Manutenção -->
        <div class="maintenance-container">
          <div v-for="item in historicos" :key="item.id" class="maintenance-card">

            <div class="maintenance-left">
              <div class="maintenance-dot" :class="eventoClasse(item.evento)"></div>
            </div>

            <div class="maintenance-content">

              <div class="maintenance-header">

                <q-chip dense square :class="'maintenance-chip ' + eventoClasse(item.evento)">
                  {{ item.evento }}
                </q-chip>

                <span class="maintenance-date">
                  <q-icon name="event" size="15px" />
                  {{ formatDate(item.data) }}
                </span>

              </div>

              <div class="maintenance-user">
                <q-icon name="person" size="17px" />
                {{ item.usuario }} — {{ item.escritorio }}
              </div>

              <div class="maintenance-description">
                {{ item.descricao }}
              </div>

            </div>

          </div>
        </div>
      </div>
  </q-card>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  historicos: { type: Array, default: () => [] },
  possuiFiltros: { type: Boolean, default: false },
  filtros: { type: Object, default: () => ({}) },
})

function texto(valor) {
  if (valor === null || valor === undefined) return ''
  if (Array.isArray(valor)) return valor.map(v => texto(v)).filter(Boolean).join(', ')
  if (typeof valor === 'object') return valor.nome || valor.label || valor.name || valor.value || ''
  return String(valor)
}

function normalizarFiltro(valor) {
  if (valor === null || valor === undefined) return []
  if (Array.isArray(valor)) return valor.map(item => texto(item).trim().toLowerCase()).filter(Boolean)
  const valorTexto = texto(valor).trim().toLowerCase()
  if (!valorTexto) return []
  return [valorTexto]
}

const filtroBuscaAplicado = computed(() => Boolean(texto(props.filtros?.busca).trim()))
const filtroEventoAplicado = computed(() => normalizarFiltro(props.filtros?.evento).length > 0)
const filtroEscritorioAplicado = computed(() => normalizarFiltro(props.filtros?.escritorio).length > 0)
const filtroDepartamentoAplicado = computed(() => normalizarFiltro(props.filtros?.departamento).length > 0)
const filtroUsuarioAplicado = computed(() => normalizarFiltro(props.filtros?.usuario).length > 0)
const filtroPeriodoAplicado = computed(() => Boolean(props.filtros?.dataInicial || props.filtros?.dataFinal))

const textoPeriodo = computed(() => {
  const inicio = texto(props.filtros?.dataInicial)
  const fim = texto(props.filtros?.dataFinal)
  if (inicio && fim) return `${inicio} — ${fim}`
  return inicio || fim || ''
})

const appliedFilters = computed(() => {
  const list = []
  if (filtroBuscaAplicado.value) list.push({ label: 'Pesquisa', value: texto(props.filtros.busca) })
  if (filtroEventoAplicado.value) list.push({ label: 'Evento', value: texto(props.filtros.evento) })
  if (filtroEscritorioAplicado.value) list.push({ label: 'Escritório', value: texto(props.filtros.escritorio) })
  if (filtroDepartamentoAplicado.value) list.push({ label: 'Departamento', value: texto(props.filtros.departamento) })
  if (filtroUsuarioAplicado.value) list.push({ label: 'Usuário', value: texto(props.filtros.usuario) })
  if (filtroPeriodoAplicado.value) list.push({ label: 'Período', value: textoPeriodo.value })
  return list
})

function formatDate(d) {
  if (!d) return ''
  const dt = new Date(d)
  if (Number.isNaN(dt.getTime())) return d
  return dt.toLocaleDateString()
}

function eventoClasse(evento) {
  if (!evento) return 'preventiva'
  const key = String(evento).toLowerCase()
  if (key.includes('manuten') || key.includes('manutenção') || key.includes('manutenc')) return 'preventiva'
  if (key.includes('moviment')) return 'corretiva'
  // default
  return 'preventiva'
}
</script>
