# CSS/SCSS アクセシビリティ: フォーカス

**このドキュメントについて**: CSS/SCSS コーディング規約 - アクセシビリティ: フォーカス

---

## 9. アクセシビリティ

### 9.1 Focus Management & キーボードナビゲーション

#### **Focus Ring & Visual Indicators**
```css
/* ✅ Good: アクセシブルなフォーカス管理 */

/* Modern focus ring system */
:root {
  --focus-ring-color: #005fcc;
  --focus-ring-width: 2px;
  --focus-ring-offset: 2px;
  --focus-ring-style: solid;
  --focus-ring-opacity: 1;
}

/* Global focus-visible implementation */
:focus {
  outline: none; /* Remove default outline */
}

:focus-visible {
  outline: var(--focus-ring-width) var(--focus-ring-style) var(--focus-ring-color);
  outline-offset: var(--focus-ring-offset);
  border-radius: 2px;
}

/* Interactive elements with enhanced focus */
button,
[role="button"],
input,
select,
textarea,
a {
  position: relative;
  transition: all 0.2s ease;
}

button:focus-visible,
[role="button"]:focus-visible {
  outline: var(--focus-ring-width) var(--focus-ring-style) var(--focus-ring-color);
  outline-offset: var(--focus-ring-offset);
  box-shadow: 0 0 0 calc(var(--focus-ring-width) + var(--focus-ring-offset)) 
              rgba(0, 95, 204, 0.2);
}

/* Form elements with consistent focus treatment */
input:focus-visible,
select:focus-visible,
textarea:focus-visible {
  border-color: var(--focus-ring-color);
  box-shadow: 0 0 0 var(--focus-ring-width) rgba(0, 95, 204, 0.2);
  outline: none;
}

/* Links with underline focus indicator */
a:focus-visible {
  outline: var(--focus-ring-width) var(--focus-ring-style) var(--focus-ring-color);
  outline-offset: var(--focus-ring-offset);
  text-decoration: underline;
  text-decoration-thickness: 2px;
  text-underline-offset: 4px;
}

/* Skip link implementation */
.skip-link {
  position: absolute;
  top: -40px;
  left: 6px;
  background: var(--focus-ring-color);
  color: white;
  padding: 8px 12px;
  font-size: 14px;
  text-decoration: none;
  border-radius: 4px;
  z-index: 9999;
  transition: top 0.3s ease;
}

.skip-link:focus {
  top: 6px;
  outline: 2px solid white;
  outline-offset: 2px;
}

/* Focus trap for modal dialogs */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal[aria-hidden="true"] {
  display: none;
}

.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
}

.modal-content:focus {
  outline: none;
}

/* Focus within for maintaining focus */
.focus-within-container:focus-within {
  box-shadow: 0 0 0 2px var(--focus-ring-color);
  border-radius: 4px;
}
```

#### **Keyboard Navigation Patterns**
```css
/* ✅ Good: キーボードナビゲーション対応 */

/* Tab order optimization */
.tab-container {
  display: flex;
  border-bottom: 1px solid #d1d5db;
}

.tab-button {
  padding: 0.75rem 1rem;
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  cursor: pointer;
  color: #6b7280;
  font-weight: 500;
  transition: all 0.2s ease;
}

.tab-button:hover {
  color: #374151;
  background-color: #f9fafb;
}

.tab-button:focus-visible {
  outline: 2px solid var(--focus-ring-color);
  outline-offset: -2px;
  color: #374151;
}

.tab-button[aria-selected="true"] {
  color: #1f2937;
  border-bottom-color: var(--focus-ring-color);
  background-color: #ffffff;
}

/* Dropdown menu with arrow key navigation */
.dropdown {
  position: relative;
  display: inline-block;
}

.dropdown-toggle {
  padding: 0.5rem 1rem;
  background: #ffffff;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.dropdown-toggle:focus-visible {
  border-color: var(--focus-ring-color);
  box-shadow: 0 0 0 2px rgba(0, 95, 204, 0.2);
  outline: none;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  background: #ffffff;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
  z-index: 100;
  min-width: 200px;
  opacity: 0;
  visibility: hidden;
  transform: translateY(-10px);
  transition: all 0.2s ease;
}

.dropdown-menu[aria-expanded="true"] {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.dropdown-item {
  display: block;
  width: 100%;
  padding: 0.75rem 1rem;
  background: none;
  border: none;
  text-align: left;
  cursor: pointer;
  color: #374151;
  transition: background-color 0.2s ease;
}

.dropdown-item:hover,
.dropdown-item:focus {
  background-color: #f3f4f6;
  outline: none;
}

.dropdown-item:focus-visible {
  background-color: #e5e7eb;
  outline: 2px solid var(--focus-ring-color);
  outline-offset: -2px;
}

/* Breadcrumb navigation */
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem 0;
}

.breadcrumb-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.breadcrumb-link {
  color: #6b7280;
  text-decoration: none;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.breadcrumb-link:hover {
  color: #374151;
  background-color: #f9fafb;
}

.breadcrumb-link:focus-visible {
  outline: 2px solid var(--focus-ring-color);
  outline-offset: 2px;
  color: #374151;
}

.breadcrumb-separator {
  color: #9ca3af;
  font-size: 0.875rem;
  user-select: none;
}

/* Current page indicator */
.breadcrumb-current {
  color: #1f2937;
  font-weight: 600;
  padding: 0.25rem 0.5rem;
}
```

### 9.2 Color Contrast & Visual Accessibility
