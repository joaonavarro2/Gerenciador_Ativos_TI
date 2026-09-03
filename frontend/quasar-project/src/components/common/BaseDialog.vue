<template>
  <q-dialog
    :model-value="modelValue"
    persistent
    transition-show="fade"
    transition-hide="fade"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <q-card class="base-dialog-card" :style="dialogStyle">
      <q-card-section class="row items-center justify-between q-pb-sm">
        <div class="row items-center">
          <q-avatar
            :color="iconBackground"
            :text-color="iconColor"
            :icon="icon"
            size="48px"
          />

          <div class="q-ml-md">
            <div class="text-h5 text-weight-bold">
              {{ title }}
            </div>

            <div v-if="subtitle" class="text-subtitle2 text-grey-7">
              {{ subtitle }}
            </div>
          </div>
        </div>

        <q-btn flat round dense icon="close" @click="fechar" />
      </q-card-section>

      <q-separator v-if="showSeparator" />

      <div class="base-dialog-body">
        <slot />
      </div>

      <div class="base-dialog-footer">
        <slot name="footer" />
      </div>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  title: {
    type: String,
    default: '',
  },
  subtitle: {
    type: String,
    default: '',
  },
  icon: {
    type: String,
    default: 'info',
  },
  iconColor: {
    type: String,
    default: 'primary',
  },
  iconBackground: {
    type: String,
    default: 'primary-1',
  },
  width: {
    type: [String, Number],
    default: '760px',
  },
  maxWidth: {
    type: [String, Number],
    default: '92vw',
  },
  maxHeight: {
    type: [String, Number],
    default: '90vh',
  },
  showSeparator: {
    type: Boolean,
    default: true,
  },
})

const emit = defineEmits(['update:modelValue'])

const dialogStyle = computed(() => ({
  width: typeof props.width === 'number' ? `${props.width}px` : props.width,
  maxWidth: typeof props.maxWidth === 'number' ? `${props.maxWidth}px` : props.maxWidth,
  maxHeight: typeof props.maxHeight === 'number' ? `${props.maxHeight}px` : props.maxHeight,
  overflow: 'hidden',
  borderRadius: '18px',
}))

function fechar() {
  emit('update:modelValue', false)
}
</script>

<style scoped>
.base-dialog-card {
  display: flex;
  flex-direction: column;
  background: #ffffff;
  max-height: 90vh;
}

.base-dialog-body {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  min-height: 0;
}

.base-dialog-footer {
  border-top: 1px solid rgba(15, 23, 42, 0.08);
  background: #ffffff;
}
</style>
