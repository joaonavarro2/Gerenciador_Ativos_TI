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

          <q-avatar
            size="56px"
            rounded
            color="red-1"
            text-color="negative"
          >

            <q-icon
              name="delete"
              size="26px"
            />

          </q-avatar>

          <div>

            <div class="delete-title">

              Excluir Bem

            </div>

            <div class="delete-subtitle">

              Esta ação é permanente e não poderá ser desfeita.

            </div>

          </div>

        </div>

        <q-btn
          flat
          round
          dense
          icon="close"
          class="delete-close"
          @click="$emit('update:modelValue', false)"
        />

      </div>

      <q-separator />

      <!-- =======================================================
           CONTEÚDO
      ======================================================== -->

      <div class="delete-content">

        <div class="warning-box">

          <q-icon
            name="warning"
            size="34px"
            color="warning"
          />

          <div>

            <div class="warning-title">

              Tem certeza que deseja excluir este bem?

            </div>

            <div class="warning-text">

              Depois da confirmação o bem será removido
              permanentemente do sistema.

            </div>

          </div>

        </div>

        <!-- ==========================================
             CARD DO BEM
        =========================================== -->

        <q-card
          flat
          bordered
          class="delete-item-card"
        >

          <div class="item-header">

            <q-avatar
              rounded
              size="52px"
              :color="bem.avatarColor"
              :text-color="bem.iconColor"
            >

              <q-icon
                :name="bem.icon"
                size="22px"
              />

            </q-avatar>

            <div>

              <div class="item-patrimonio">

                {{ bem.patrimonio }}

              </div>

              <div class="item-nome">

                {{ bem.nome }}

              </div>

            </div>

          </div>

          <div class="item-grid">

            <div class="item-info">

              <span class="info-label">

                Categoria

              </span>

              <span class="info-value">

                {{ bem.categoria }}

              </span>

            </div>

            <div class="item-info">

              <span class="info-label">

                Departamento

              </span>

              <span class="info-value">

                {{ bem.departamento }}

              </span>

            </div>

            <div class="item-info">

              <span class="info-label">

                Responsável

              </span>

              <span class="info-value">

                {{ bem.responsavel }}

              </span>

            </div>

            <div class="item-info">

              <span class="info-label">

                Status

              </span>

              <q-chip
                dense
                square
                color="green-1"
                text-color="green-8"
              >

                {{ bem.status }}

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

        <q-btn
          outline
          color="grey-7"
          label="Cancelar"
          @click="$emit('update:modelValue', false)"
        />

        <q-btn
          color="negative"
          icon="delete"
          label="Excluir Bem"
          @click="confirmarDelete"
        />

      </div>

    </q-card>

  </q-dialog>

</template>

<script setup>

import { ref } from 'vue'

/* ==========================================================
   PROPS
========================================================== */

defineProps({

  modelValue: Boolean,

  bem: {
    type: Object,
    default: () => ({})
  }

})

const emit = defineEmits([
  'update:modelValue',
  'deleted'
])

/* ==========================================================
   MOCK
==========================================================

BACKEND

GET /api/bens/{id}

Quando o backend estiver pronto,
este objeto será removido e todas
as informações serão recebidas
através da prop "bem".

========================================================== */

const mockBem = ref({

  id: 1,

  patrimonio: 'BEM-0041',

  nome: 'Trator Agrícola MF 275',

  categoria: 'Veículo',

  departamento: 'Operações',

  responsavel: 'Carlos Andrade',

  status: 'Ativo',

  icon: 'agriculture',

  avatarColor: 'green-1',

  iconColor: 'green-8'

})

/* ==========================================================
   DADOS EXIBIDOS
==========================================================

Enquanto não existir backend,
utilizamos o mock.

Posteriormente:

const dadosBem = computed(() => bem)

========================================================== */

// eslint-disable-next-line vue/no-dupe-keys
const bem = mockBem

/* ==========================================================
   EXCLUIR
========================================================== */

function confirmarDelete() {

  /*
  =====================================================

  BACKEND

  DELETE /api/bens/{id}

  Exemplo:

  await api.delete(`/bens/${bem.value.id}`)

  Após sucesso:

  emit('deleted', bem.value.id)

  emit('update:modelValue', false)

  Notify.create({
      type:'positive',
      message:'Bem excluído com sucesso.'
  })

  =====================================================
  */

  console.log('Excluir bem:', bem.value.id)

  emit('deleted', bem.value.id)

  emit('update:modelValue', false)

}

</script>