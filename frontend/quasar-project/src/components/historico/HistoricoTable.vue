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

            <q-btn flat round dense icon="visibility" color="primary" aria-label="Visualizar operação" @click="abrirDetalhes(item)">
              <q-tooltip>Visualizar operação</q-tooltip>
            </q-btn>
          </div>
        </div>
      </div>

    <q-dialog v-model="dialogDetalhes">
      <q-card class="historico-dialog-card">
        <q-card-section class="row items-center justify-between q-pb-sm">
          <div class="row items-center">
            <q-avatar color="green-1" text-color="teal" icon="visibility" size="48px" />
            <div class="q-ml-md">
              <div class="text-h5 text-weight-bold">Detalhes da operação</div>
              <div class="text-subtitle2 text-grey-7">Informações do registro selecionado no histórico.</div>
            </div>
          </div>
          <q-space />
          <q-btn flat round dense icon="close" v-close-popup aria-label="Fechar" />
        </q-card-section>
        <q-separator />
        <q-card-section v-if="registroSelecionado" class="dialog-scroll">
          <div class="historico-secao">
            <div class="titulo-secao row items-center q-mb-md">
              <q-icon name="inventory_2" color="positive" size="20px" />
              <div class="q-ml-sm">{{ registroSelecionado.evento }}</div>
            </div>
            <div class="info-grid">
              <div class="info-item">
                <q-icon name="tag" class="info-icon" />
                <div><div class="info-label">BEM / PATRIMÔNIO</div><div class="info-value">{{ registroSelecionado.ativoId || '—' }}</div></div>
              </div>
              <div class="info-item">
                <q-icon name="event" class="info-icon" />
                <div><div class="info-label">DATA</div><div class="info-value">{{ formatDate(registroSelecionado.data) || '—' }}</div></div>
              </div>
              <div class="info-item full-width">
                <q-icon name="description" class="info-icon" />
                <div><div class="info-label">DESCRIÇÃO</div><div class="info-value">{{ registroSelecionado.descricao || '—' }}</div></div>
              </div>
              <div class="info-item" v-for="(valor, chave) in registroSelecionado.detalhes" :key="chave">
                <q-icon :name="iconeDetalhe(chave)" class="info-icon" />
                <div><div class="info-label">{{ chave }}</div><div class="info-value">{{ formatarDetalhe(valor) }}</div></div>
              </div>
            </div>
          </div>
        </q-card-section>
        <q-card-actions align="right"><q-btn flat label="Fechar" color="primary" v-close-popup /></q-card-actions>
      </q-card>
    </q-dialog>
  </q-card>
</template>

<script setup>
import { computed, ref } from 'vue'

const dialogDetalhes = ref(false)
const registroSelecionado = ref(null)

function abrirDetalhes(item) {
  registroSelecionado.value = item
  dialogDetalhes.value = true
}

function formatarDetalhe(valor) {
  if (valor === null || valor === undefined || valor === '') return '—'
  if (typeof valor === 'object') return JSON.stringify(valor)
  return String(valor)
}

function iconeDetalhe(chave) {
  const campo = String(chave).toLowerCase()
  if (campo.includes('data')) return 'event'
  if (campo.includes('pessoa') || campo.includes('respons') || campo.includes('técnico') || campo.includes('usuário')) return 'person'
  if (campo.includes('departamento') || campo.includes('escritório')) return 'business'
  if (campo.includes('status')) return 'info'
  if (campo.includes('problema') || campo.includes('justificativa')) return 'report_problem'
  if (campo.includes('solução')) return 'build'
  return 'description'
}

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

<style scoped>
.historico-dialog-card { width: min(900px, 92vw); max-width: 92vw; max-height: 90vh; border-radius: 18px; overflow: hidden; display: flex; flex-direction: column; }
.dialog-scroll { overflow-y: auto; flex: 1 1 auto; }
.historico-secao { border-top: 1px solid #e5e7eb; padding-top: 20px; }
.titulo-secao { font-size: 14px; font-weight: 700; letter-spacing: .06em; text-transform: uppercase; color: #1f2937; gap: 8px; }
.info-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 18px 24px; }
.info-item { display: flex; align-items: flex-start; gap: 12px; min-height: 60px; }
.info-item.full-width { grid-column: 1 / -1; }
.info-icon { color: #16a34a; font-size: 22px; margin-top: 2px; }
.info-label { font-size: 11px; line-height: 1.2; color: #6b7280; letter-spacing: .08em; margin-bottom: 6px; text-transform: uppercase; font-weight: 700; }
.info-value { color: #111827; font-size: 15px; line-height: 1.5; font-weight: 500; white-space: pre-wrap; overflow-wrap: anywhere; }
@media (max-width: 600px) { .info-grid { grid-template-columns: 1fr; } .info-item.full-width { grid-column: auto; } }
</style>

