import { hasPermission } from '@/utils/permission'

const evaluateBinding = (binding) => {
  if (typeof binding.value === 'string') {
    return hasPermission(binding.value)
  }

  if (Array.isArray(binding.value)) {
    return hasPermission(binding.value)
  }

  if (binding.value && typeof binding.value === 'object') {
    const { permissions, mode = 'some' } = binding.value
    return hasPermission(permissions, mode)
  }

  return true
}

export const permissionDirective = {
  mounted(el, binding) {
    if (!evaluateBinding(binding)) {
      el.parentNode?.removeChild(el)
    }
  }
}
