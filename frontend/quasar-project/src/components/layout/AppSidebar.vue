<template>
  <q-drawer
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"

    :mini="$q.screen.gt.sm ? mini : false"

    show-if-above

    bordered

    :width="260"

    :mini-width="72"

    class="sidebar bg-white"
  >
    <q-scroll-area class="fit">

      <q-list class="q-pt-md">

        <!-- DASHBOARD -->

        <q-item clickable to="/dashboard"
          active-class="menu-active"
        >
          <q-item-section avatar>
            <q-icon
              name="dashboard"
              class="menu-icon"
            />
          </q-item-section>

          <q-item-section
            v-if="!mini"
            class="menu-text"
          >
            Dashboard
          </q-item-section>
        </q-item>

        <q-separator class="q-my-md" />

        <!-- PATRIMÔNIO -->

        <div
          v-if="!mini"
          class="menu-title"
        >
          PATRIMÔNIO
        </div>

        <q-item clickable to="/bens">
          <q-item-section avatar>
            <q-icon
              name="inventory_2"
              class="menu-icon"
            />
          </q-item-section>

          <q-item-section
            class="menu-text"
            v-if="!mini"
          >
            Bens / Ativos
          </q-item-section>
        </q-item>

        <q-item clickable
                v-ripple
                @click="irPara('/movimentacoes')">
          <q-item-section avatar>
            <q-icon
              name="sync_alt"
              class="menu-icon"
            />
          </q-item-section>

          <q-item-section
            class="menu-text"
            v-if="!mini"
          >
            Movimentações
          </q-item-section>
        </q-item>

        <q-item clickable>
          <q-item-section avatar>
            <q-icon
              name="schedule"
              class="menu-icon"
            />
          </q-item-section>

          <q-item-section
            class="menu-text"
            v-if="!mini"
          >
            Históricos
          </q-item-section>
        </q-item>

        <q-item clickable>
          <q-item-section avatar>
            <q-icon
              name="build"
              class="menu-icon"
            />
          </q-item-section>

          <q-item-section
            class="menu-text"
            v-if="!mini"
          >
            Consertos
          </q-item-section>
        </q-item>

        <q-separator class="q-my-md" />

        <!-- CADASTROS -->

        <div
          v-if="!mini"
          class="menu-title"
        >
          CADASTROS
        </div>

        <q-item clickable>
          <q-item-section avatar>
            <q-icon
              name="business"
              class="menu-icon"
            />
          </q-item-section>

          <q-item-section
            class="menu-text"
            v-if="!mini"
          >
            Escritórios
          </q-item-section>
        </q-item>

        <q-item clickable>
          <q-item-section avatar>
            <q-icon
              name="hub"
              class="menu-icon"
            />
          </q-item-section>

          <q-item-section
            class="menu-text"
            v-if="!mini"
          >
            Departamentos
          </q-item-section>
        </q-item>

        <q-item clickable>
          <q-item-section avatar>
            <q-icon
              name="group"
              class="menu-icon"
            />
          </q-item-section>

          <q-item-section
            class="menu-text"
            v-if="!mini"
          >
            Pessoas
          </q-item-section>
        </q-item>

        <q-item clickable>
          <q-item-section avatar>
            <q-icon
              name="folder_open"
              class="menu-icon"
            />
          </q-item-section>

          <q-item-section
            class="menu-text"
            v-if="!mini"
          >
            Outros Itens
          </q-item-section>
        </q-item>

      </q-list>

      <div class="sidebar-footer">

        <q-item clickable>

          <q-item-section avatar>
            <q-icon
              name="logout"
              class="menu-icon"
            />
          </q-item-section>

          <q-item-section
            class="menu-text"
            v-if="!mini"
          >
            Sair
          </q-item-section>

        </q-item>

      </div>

    </q-scroll-area>
  </q-drawer>
</template>

<script setup>
import { useQuasar } from 'quasar'
import { useRouter } from 'vue-router'

const $q = useQuasar()
defineProps({
  modelValue: Boolean,
  mini: Boolean
})

defineEmits([
  'update:modelValue'
])

const router = useRouter()

function irPara(caminho) {
  router.push(caminho)
}

</script>