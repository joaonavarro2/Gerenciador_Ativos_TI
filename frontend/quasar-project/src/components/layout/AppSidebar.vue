<template>
  <q-drawer
    :model-value="true"
    bordered
    :mini="!modelValue"
    :width="260"
    :mini-width="72"
    class="sidebar bg-white"
  >
    <q-scroll-area class="fit">

      <q-list class="q-pt-md">

        <!-- DASHBOARD -->

        <q-item
          clickable
          active
          active-class="menu-active"
        >
          <q-item-section avatar>
            <q-icon
              name="dashboard"
              class="menu-icon"
            />
          </q-item-section>

          <q-item-section
            v-if="modelValue"
            class="menu-text"
          >
            Dashboard
          </q-item-section>
        </q-item>

        <q-separator class="q-my-md" />

        <!-- PATRIMÔNIO -->

        <div
          v-if="modelValue"
          class="menu-title"
        >
          PATRIMÔNIO
        </div>

        <q-item clickable>
          <q-item-section avatar>
            <q-icon
              name="inventory_2"
              class="menu-icon"
            />
          </q-item-section>

          <q-item-section
            class="menu-text"
            v-if="modelValue"
          >
            Bens / Ativos
          </q-item-section>
        </q-item>

        <q-item clickable>
          <q-item-section avatar>
            <q-icon
              name="sync_alt"
              class="menu-icon"
            />
          </q-item-section>

          <q-item-section
            class="menu-text"
            v-if="modelValue"
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
            v-if="modelValue"
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
            v-if="modelValue"
          >
            Consertos
          </q-item-section>
        </q-item>

        <q-separator class="q-my-md" />

        <!-- CADASTROS -->

        <div
          v-if="modelValue"
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
            v-if="modelValue"
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
            v-if="modelValue"
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
            v-if="modelValue"
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
            v-if="modelValue"
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
            v-if="modelValue"
          >
            Sair
          </q-item-section>

        </q-item>

      </div>

    </q-scroll-area>
  </q-drawer>
</template>

<script setup>
defineProps({
  modelValue: Boolean
})
</script>