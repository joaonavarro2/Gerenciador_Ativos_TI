<template>
  <q-dialog
    :model-value="modelValue"
    persistent
    transition-show="fade"
    transition-hide="fade"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <q-card class="delete-dialog">
      <!-- =======================================================
           HEADER
      ======================================================== -->

      <div class="delete-header">
        <div class="delete-header-left">
          <q-avatar size="56px" rounded color="red-1" text-color="negative">
            <q-icon name="delete" size="26px" />
          </q-avatar>

          <div>
            <div class="delete-title">Excluir Bem</div>

            <div class="delete-subtitle">
              O bem será arquivado e deixará de aparecer no inventário.
            </div>
          </div>
        </div>

        <q-btn flat round dense icon="close" class="delete-close" @click="fecharDialog" />
      </div>

      <q-separator />

      <!-- =======================================================
           CONTEÚDO
      ======================================================== -->

      <div class="delete-content">
        <div class="warning-box">
          <q-icon name="warning" size="34px" color="warning" />

          <div>
            <div class="warning-title">Tem certeza que deseja arquivar este bem?</div>

            <div class="warning-text">
              O histórico de movimentações e manutenções será preservado para auditoria.
            </div>
          </div>
        </div>

        <!-- =====================================================
             CARD DO BEM
        ====================================================== -->

        <q-card v-if="bem" flat bordered class="delete-item-card">
          <div class="item-header">
            <q-avatar
              rounded
              size="52px"
              :color="bem.avatarColor || 'grey-2'"
              :text-color="bem.iconColor || 'grey-8'"
            >
              <q-icon :name="bem.icon || 'inventory_2'" size="22px" />
            </q-avatar>

            <div>
              <div class="item-patrimonio">
                {{ bem.patrimonio || bem.id }}
              </div>

              <div class="item-nome">
                {{ bem.nome || bem.descricao }}
              </div>
            </div>
          </div>

          <div class="item-grid">
            <div class="item-info">
              <span class="info-label"> Categoria </span>

              <span class="info-value">
                {{ bem.categoria || '-' }}
              </span>
            </div>

            <div class="item-info">
              <span class="info-label"> Departamento </span>

              <span class="info-value">
                {{ bem.departamento || '-' }}
              </span>
            </div>

            <div class="item-info">
              <span class="info-label"> Responsável </span>

              <span class="info-value">
                {{ bem.responsavel || '-' }}
              </span>
            </div>

            <div class="item-info">
              <span class="info-label"> Status </span>

              <q-chip dense square :class="statusClass(bem.status)">
                {{ bem.status || '-' }}
              </q-chip>
            </div>
          </div>
        </q-card>
      </div>

      <q-separator />

      <!-- =======================================================
           FOOTER
      ======================================================== -->

      <div class="delete-footer">
        <q-btn outline color="grey-7" label="Cancelar" @click="fecharDialog" />

        <q-btn
          color="negative"
          icon="inventory_2"
          label="Arquivar Bem"
          :disable="!bem || !bem.id"
          @click="confirmarDelete"
        />
      </div>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { useQuasar } from 'quasar'
import { api } from '@/boot/axios'

const $q = useQuasar()

/* ==========================================================
   PROPS
========================================================== */

const props = defineProps({
  /*
   * Controla a abertura do diálogo.
   */
  modelValue: {
    type: Boolean,
    default: false,
  },

  /*
   * Bem selecionado na tabela.
   *
   * IMPORTANTE:
   *
   * Agora NÃO existe mais mock
   * dentro deste componente.
   *
   * O bem vem diretamente do BensTable.
   */
  bem: {
    type: Object,
    default: null,
  },
})

/* ==========================================================
   EMITS
========================================================== */

const emit = defineEmits([
  'update:modelValue',

  /*
   * Envia o ID do bem para o BensTable.
   */
  'remover-bem',
])

/* ==========================================================
   FECHAR
========================================================== */

function fecharDialog() {
  emit('update:modelValue', false)
}

/* ==========================================================
   STATUS
========================================================== */

function statusClass(status) {
  switch (status) {
    case 'Ativo':
      return 'status-chip status-ativo'

    case 'Em Manutenção':
      return 'status-chip status-manutencao'

    case 'Inativo':
      return 'status-chip status-inativo'

    case 'Descartado':
      return 'status-chip status-descartado'

    default:
      return 'status-chip'
  }
}

/* ==========================================================
   CONFIRMAR EXCLUSÃO
========================================================== */

async function confirmarDelete() {
  /*
   * Segurança:
   *
   * Se nenhum bem estiver selecionado,
   * não fazemos nada.
   */

  if (!props.bem || !props.bem.id) {
    $q.notify({
      type: 'negative',

      message: 'Nenhum bem selecionado para exclusão.',
    })

    return
  }

  const id = props.bem.id

  try {
    await api.delete(`/bens/${id}`)
    emit('remover-bem', id)
    emit('update:modelValue', false)
    $q.notify({ type: 'positive', message: 'Bem arquivado; histórico preservado.' })
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.mensagem || 'Não foi possível arquivar o bem.',
    })
  }
}
</script>
