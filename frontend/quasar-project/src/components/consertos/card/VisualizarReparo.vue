<template>
  <q-dialog
    :model-value="modelValue"
    persistent
    transition-show="fade"
    transition-hide="fade"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <q-card class="reparo-dialog-card">
      <q-card-section class="row items-center justify-between q-pb-sm">
        <div class="row items-center">
          <q-avatar color="green-1" text-color="teal" icon="visibility" size="48px" />

          <div class="q-ml-md">
            <div class="text-h5 text-weight-bold">Detalhes do Reparo</div>
            <div class="text-subtitle2 text-grey-7">Informações gerais do serviço técnico registrado.</div>
          </div>
        </div>

        <q-btn flat round dense icon="close" @click="$emit('update:modelValue', false)" />
      </q-card-section>

      <q-separator />

      <q-card-section class="dialog-scroll">
        <div class="reparo-secao">
          <div class="titulo-secao row items-center q-mb-md">
            <q-icon name="build_circle" color="positive" size="20px" />
            <div class="q-ml-sm">ATIVO E TIPO DE SERVIÇO</div>
          </div>

          <div class="info-grid">
            <div class="info-item">
              <q-icon name="tag" class="info-icon" />
              <div>
                <div class="info-label">ID DO ATIVO</div>
                <div class="info-value">{{ reparo.ativoId || '-' }}</div>
              </div>
            </div>

            <div class="info-item">
              <q-icon name="engineering" class="info-icon" />
              <div>
                <div class="info-label">TIPO DE REPARO</div>
                <div class="info-value">{{ reparo.tipo || '-' }}</div>
              </div>
            </div>

            <div class="info-item full-width">
              <q-icon name="report_problem" class="info-icon" />
              <div>
                <div class="info-label">DESCRIÇÃO DO PROBLEMA</div>
                <div class="info-value">{{ reparo.problema || '-' }}</div>
              </div>
            </div>

            <div class="info-item full-width">
              <q-icon name="notes" class="info-icon" />
              <div>
                <div class="info-label">DETALHES</div>
                <div class="info-value">{{ reparo.descricao || 'Sem detalhes adicionais.' }}</div>
              </div>
            </div>
          </div>
        </div>

        <div class="reparo-secao q-mt-xl">
          <div class="titulo-secao row items-center q-mb-md">
            <q-icon name="person" color="positive" size="20px" />
            <div class="q-ml-sm">RESPONSÁVEL TÉCNICO</div>
          </div>

          <div class="info-grid">
            <div class="info-item">
              <q-icon name="person" class="info-icon" />
              <div>
                <div class="info-label">TÉCNICO</div>
                <div class="info-value">{{ reparo.tecnico || '-' }}</div>
              </div>
            </div>
          </div>
        </div>

        <div class="reparo-secao q-mt-xl">
          <div class="titulo-secao row items-center q-mb-md">
            <q-icon name="event" color="positive" size="20px" />
            <div class="q-ml-sm">CRONOGRAMA</div>
          </div>

          <div class="info-grid">
            <div class="info-item">
              <q-icon name="calendar_today" class="info-icon" />
              <div>
                <div class="info-label">DATA DE INÍCIO</div>
                <div class="info-value">{{ reparo.dataInicio || '-' }}</div>
              </div>
            </div>

            <div class="info-item">
              <q-icon name="task_alt" class="info-icon" />
              <div>
                <div class="info-label">DATA DE CONCLUSÃO</div>
                <div class="info-value">{{ reparo.dataConclusao || '-' }}</div>
              </div>
            </div>

            <div class="info-item">
              <q-icon name="info" class="info-icon" />
              <div>
                <div class="info-label">STATUS</div>
                <div class="info-value">{{ reparo.status || 'Pendente' }}</div>
              </div>
            </div>
          </div>
        </div>

        <div class="reparo-secao q-mt-xl">
          <div class="titulo-secao row items-center q-mb-md">
            <q-icon name="attach_file" color="positive" size="20px" />
            <div class="q-ml-sm">ANEXOS</div>
          </div>
          <q-list v-if="anexos.length" bordered separator>
            <q-item v-for="anexo in anexos" :key="anexo.id">
              <q-item-section avatar><q-icon name="description" color="positive" /></q-item-section>
              <q-item-section>
                <q-item-label>{{ anexo.nomeArquivo }}</q-item-label>
                <q-item-label caption>{{ formatarTamanho(anexo.tamanho) }}</q-item-label>
              </q-item-section>
              <q-item-section side>
                <q-btn flat round dense icon="download" :loading="baixando === anexo.id" @click="baixar(anexo)" />
              </q-item-section>
            </q-item>
          </q-list>
          <div v-else class="text-grey-7">{{ carregandoAnexos ? 'Carregando anexos…' : 'Nenhum anexo.' }}</div>
        </div>
      </q-card-section>

      <q-card-actions align="right" class="q-pa-md dialog-actions">
        <q-btn flat no-caps label="Fechar" @click="$emit('update:modelValue', false)" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { api } from '@/boot/axios.js'

const props = defineProps({
  modelValue: Boolean,
  reparo: {
    type: Object,
    default: () => ({})
  }
})

defineEmits(['update:modelValue'])

const anexos = ref([])
const carregandoAnexos = ref(false)
const baixando = ref(null)

watch(() => [props.modelValue, props.reparo?.id], async ([aberto, id]) => {
  if (!aberto || !id) return
  carregandoAnexos.value = true
  try {
    const { data } = await api.get(`/consertos/${id}/anexos`)
    anexos.value = data
  } catch { anexos.value = [] } finally { carregandoAnexos.value = false }
}, { immediate: true })

async function baixar(anexo) {
  baixando.value = anexo.id
  try {
    const { data } = await api.get(`/consertos/${props.reparo.id}/anexos/${anexo.id}`, { responseType: 'blob' })
    const url = URL.createObjectURL(data)
    const link = document.createElement('a')
    link.href = url
    link.download = anexo.nomeArquivo
    link.click()
    URL.revokeObjectURL(url)
  } finally { baixando.value = null }
}

function formatarTamanho(bytes) {
  return bytes < 1024 * 1024 ? `${Math.ceil(bytes / 1024)} KB` : `${(bytes / (1024 * 1024)).toFixed(1)} MB`
}
</script>

<style scoped>
.reparo-dialog-card {
  width: min(900px, 92vw);
  max-width: 92vw;
  max-height: 90vh;
  border-radius: 18px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.dialog-scroll {
  overflow-y: auto;
  flex: 1 1 auto;
}

.dialog-actions {
  border-top: 1px solid #e5e7eb;
  background: #fff;
  position: sticky;
  bottom: 0;
}

.reparo-secao {
  border-top: 1px solid #e5e7eb;
  padding-top: 20px;
}

.reparo-secao:first-child {
  border-top: none;
  padding-top: 0;
}

.titulo-secao {
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: #1f2937;
  gap: 8px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px 24px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  min-height: 70px;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-icon {
  color: #16a34a;
  font-size: 22px;
  margin-top: 2px;
}

.info-label {
  font-size: 11px;
  line-height: 1.2;
  color: #6b7280;
  letter-spacing: 0.08em;
  margin-bottom: 6px;
  text-transform: uppercase;
  font-weight: 700;
}

.info-value {
  color: #111827;
  font-size: 15px;
  line-height: 1.5;
  font-weight: 500;
}
</style>
