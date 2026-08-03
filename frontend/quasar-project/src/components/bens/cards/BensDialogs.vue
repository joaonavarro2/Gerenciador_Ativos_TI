<template>

  <q-dialog
    :model-value="modelValue"
    persistent
    transition-show="fade"
    transition-hide="fade"
    @update:model-value="$emit('update:modelValue', $event)"
  >

    <q-card class="bem-dialog"  style="
    width:900px;
    height:500px;
    max-width:none;
    max-height:none;
    background:white;
    border-radius:10px;
  ">

      <!-- =======================================================
           HEADER
      ======================================================== -->

      <div class="dialog-header">

        <div class="dialog-header-left">

          <q-avatar
            size="56px"
            rounded
            :color="bem.avatarColor"
            :text-color="bem.iconColor"
          >

            <q-icon
              :name="bem.icon"
              size="24px"
            />

          </q-avatar>

          <div>

            <div class="dialog-badges">

              <q-chip
                dense
                square
                class="badge-id"
              >
                {{ bem.patrimonio }}
              </q-chip>

              <q-chip
                dense
                :class="statusClass(bem.status)"
              >
                {{ bem.status }}
              </q-chip>

            </div>

            <div class="dialog-title">
              {{ bem.nome }}
            </div>

            <div class="dialog-location">

              <q-icon
                name="place"
                size="15px"
              />
                {{ bem.departamento }}
                •
                {{ bem.localizacao }}
                •
                {{ bem.escritorio }}

            </div>

          </div>

        </div>

        <q-btn
          flat
          round
          dense
          icon="close"
          class="dialog-close"
          @click="$emit('update:modelValue', false)"
        />

      </div>

      <!-- =======================================================
           ABAS
      ======================================================== -->

      <q-tabs
        v-model="tab"
        indicator-color="primary"
        active-color="primary"
        align="left"
        dense
        class="dialog-tabs"
      >

        <q-tab
          name="informacoes"
          label="Informações Gerais"
        />

        <q-tab
          name="manutencoes"
          label="Histórico de Manutenção"
        />

        <q-tab
          name="movimentacoes"
          label="Movimentações Recentes"
        />

      </q-tabs>

      <q-separator />

      <!-- =======================================================
           CONTEÚDO
      ======================================================== -->

      <q-tab-panels
        v-model="tab"
        animated
        class="dialog-content"
      >

        <q-tab-panel name="informacoes">

          <!-- COMPONENTE -->
          <BemInfoTab />

        </q-tab-panel>

        <q-tab-panel name="manutencoes">

          <!-- COMPONENTE -->
          <BemMaintenanceTab />

        </q-tab-panel>

        <q-tab-panel name="movimentacoes">

          <!-- COMPONENTE -->
          <BemMovementsTab />

        </q-tab-panel>

      </q-tab-panels>

    </q-card>

  </q-dialog>

</template>

<script setup>

import { ref } from 'vue'

import BemInfoTab from '../cards/BemInfoTab.vue'
import BemMaintenanceTab from '../cards/BemMaintenanceTab.vue'
import BemMovementsTab from '../cards/BemMovementsTab.vue'

// eslint-disable-next-line no-unused-vars
const props = defineProps({

  modelValue: Boolean,

  bemId: {
    type: String,
    default: null
  }

})

defineEmits([
  'update:modelValue'
])

const tab = ref('informacoes')

/* ==========================================================
   BACKEND
==========================================================

GET /api/bens/{id}

Quando o backend estiver pronto,
este objeto será substituído
pela resposta da API.

========================================================== */

const bem = ref({

  id: 1,

  patrimonio: 'BEM-0041',

  nome: 'Trator Agrícola MF 275',

  status: 'Ativo',

  escritorio: 'Sede',

  departamento: 'Operações',

  localizacao: 'Galpão Central',

  icon: 'agriculture',

  avatarColor: 'green-1',

  iconColor: 'green-8'

})

/* ==========================================================
   STATUS
========================================================== */

function statusClass(status) {

  switch (status) {

    case 'Ativo':
      return 'badge-status status-ativo'

    case 'Em Manutenção':
      return 'badge-status status-manutencao'

    case 'Inativo':
      return 'badge-status status-inativo'

    case 'Descartado':
      return 'badge-status status-descartado'

    default:
      return 'badge-status'

  }

}

</script>