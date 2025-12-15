<template>
  <BButton
    v-bind="$attrs"
    :variant="variant"
    :size="size"
    :class="buttonClass"
    @click="onClick"
  >
    <template v-if="opened">
      <i :class="['bi', openedIcon, 'fs-3']" />
    </template>

    <template v-else>
      {{ label }}
    </template>
  </BButton>
</template>

<script setup lang="ts">
import { computed } from 'vue';

// i18n
import { useI18n } from 'vue-i18n';

// types
import type { BaseButtonVariant, BaseSize } from 'bootstrap-vue-next';

const props = defineProps<{
  opened: boolean;
  label?: string;
  variant?: keyof BaseButtonVariant;
  size?: keyof BaseSize;
  openedIcon?: string;
  buttonClass?: string | string[];
}>();

const emit = defineEmits<{
  (e: 'update:opened', value: boolean): void;
  (e: 'toggle', value: boolean): void;
}>();

const { t } = useI18n();

const variant = computed(() => props.variant ?? 'link');
const size = computed(() => props.size ?? undefined);
const label = computed(() => (props.label ? t(props.label) : t('common.change')));
const openedIcon = computed(() => props.openedIcon ?? 'bi-x');
const buttonClass = computed(() => props.buttonClass ?? 'p-0 text-decoration-none');

function onClick() {
  const next = !props.opened;
  emit('update:opened', next);
  emit('toggle', next);
}
</script>
