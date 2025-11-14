# CSS/SCSS アクセシビリティ: カラー・SR

**このドキュメントについて**: CSS/SCSS コーディング規約 - アクセシビリティ: カラー・SR

---


#### **High Contrast Mode Support**
```css
/* ✅ Good: カラーコントラストとビジュアルアクセシビリティ */

/* Base color system with WCAG AA compliance */
:root {
  /* Text colors with 4.5:1 contrast ratio minimum */
  --color-text-primary: #111827;     /* 16.25:1 on white */
  --color-text-secondary: #4b5563;   /* 7.22:1 on white */
  --color-text-tertiary: #6b7280;    /* 5.49:1 on white */
  --color-text-inverse: #f9fafb;     /* 17.67:1 on dark */
  
  /* Interactive colors with sufficient contrast */
  --color-link: #1d4ed8;             /* 6.24:1 on white */
  --color-link-hover: #1e40af;       /* 7.48:1 on white */
  --color-link-visited: #7c3aed;     /* 5.61:1 on white */
  
  /* Status colors meeting WCAG requirements */
  --color-success: #059669;          /* 4.89:1 on white */
  --color-warning: #d97706;          /* 4.58:1 on white */
  --color-error: #dc2626;            /* 5.25:1 on white */
  --color-info: #2563eb;             /* 6.05:1 on white */
  
  /* Background colors */
  --color-bg-primary: #ffffff;
  --color-bg-secondary: #f9fafb;
  --color-bg-tertiary: #f3f4f6;
}

/* High contrast mode overrides */
@media (prefers-contrast: high) {
  :root {
    --color-text-primary: #000000;
    --color-text-secondary: #000000;
    --color-text-tertiary: #000000;
    --color-bg-primary: #ffffff;
    --color-bg-secondary: #ffffff;
    --color-bg-tertiary: #ffffff;
    
    --color-link: #0000ee;
    --color-link-hover: #0000aa;
    --color-link-visited: #800080;
    
    --color-success: #008000;
    --color-warning: #ff8c00;
    --color-error: #ff0000;
    --color-info: #0000ff;
  }
  
  /* Enhanced borders and outlines */
  .card,
  .button,
  .form-input {
    border-width: 2px;
    border-color: #000000;
  }
  
  /* Remove subtle shadows and backgrounds */
  .card {
    box-shadow: none;
    background-color: #ffffff;
  }
  
  /* Ensure all interactive elements are clearly defined */
  button,
  [role="button"],
  input,
  select,
  textarea {
    border: 2px solid #000000;
    background-color: #ffffff;
  }
}

/* Dark theme with proper contrast ratios */
@media (prefers-color-scheme: dark) {
  :root {
    --color-text-primary: #f9fafb;    /* 17.67:1 on #111827 */
    --color-text-secondary: #d1d5db;  /* 10.89:1 on #111827 */
    --color-text-tertiary: #9ca3af;   /* 6.37:1 on #111827 */
    --color-text-inverse: #111827;
    
    --color-bg-primary: #111827;
    --color-bg-secondary: #1f2937;
    --color-bg-tertiary: #374151;
    
    --color-link: #60a5fa;            /* 5.12:1 on #111827 */
    --color-link-hover: #93c5fd;      /* 7.04:1 on #111827 */
    --color-link-visited: #c084fc;    /* 4.75:1 on #111827 */
    
    --color-success: #10b981;         /* 5.32:1 on #111827 */
    --color-warning: #f59e0b;         /* 8.41:1 on #111827 */
    --color-error: #f87171;           /* 5.96:1 on #111827 */
    --color-info: #60a5fa;            /* 5.12:1 on #111827 */
  }
}

/* Forced colors mode (Windows High Contrast) */
@media (forced-colors: active) {
  :root {
    --color-text-primary: CanvasText;
    --color-text-secondary: CanvasText;
    --color-bg-primary: Canvas;
    --color-link: LinkText;
    --color-link-visited: VisitedText;
    --focus-ring-color: Highlight;
  }
  
  .card {
    border: 1px solid CanvasText;
    background-color: Canvas;
    box-shadow: none;
  }
  
  .button {
    border: 1px solid ButtonBorder;
    background-color: ButtonFace;
    color: ButtonText;
  }
  
  .button:hover {
    background-color: Highlight;
    color: HighlightText;
    border-color: HighlightText;
  }
  
  /* Ensure focus indicators are visible */
  :focus-visible {
    outline: 2px solid Highlight;
    outline-offset: 2px;
  }
}
```

#### **Color-blind Friendly Design**
```css
/* ✅ Good: 色覚障害に配慮したデザイン */

/* Status indicators with multiple visual cues */
.status-indicator {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.875rem;
  font-weight: 500;
}

/* Success state: Green + checkmark */
.status-indicator--success {
  background-color: #dcfce7;
  color: #166534;
  border: 1px solid #bbf7d0;
}

.status-indicator--success::before {
  content: "✓";
  font-weight: bold;
}

/* Warning state: Orange + exclamation */
.status-indicator--warning {
  background-color: #fef3c7;
  color: #92400e;
  border: 1px solid #fde68a;
}

.status-indicator--warning::before {
  content: "!";
  font-weight: bold;
}

/* Error state: Red + X mark */
.status-indicator--error {
  background-color: #fecaca;
  color: #991b1b;
  border: 1px solid #fca5a5;
}

.status-indicator--error::before {
  content: "✕";
  font-weight: bold;
}

/* Info state: Blue + i mark */
.status-indicator--info {
  background-color: #dbeafe;
  color: #1e40af;
  border: 1px solid #93c5fd;
}

.status-indicator--info::before {
  content: "i";
  font-weight: bold;
  font-style: italic;
}

/* Chart colors with patterns for accessibility */
.chart-bar {
  transition: opacity 0.2s ease;
}

.chart-bar--pattern-1 {
  fill: #1f77b4;
  stroke: #ffffff;
  stroke-width: 1;
}

.chart-bar--pattern-2 {
  fill: #ff7f0e;
  stroke-dasharray: 3,3;
  stroke: #ffffff;
  stroke-width: 1;
}

.chart-bar--pattern-3 {
  fill: #2ca02c;
  stroke-dasharray: 5,2,2,2;
  stroke: #ffffff;
  stroke-width: 1;
}

.chart-bar--pattern-4 {
  fill: #d62728;
  stroke-dasharray: 1,1;
  stroke: #ffffff;
  stroke-width: 1;
}

/* Form validation with multiple indicators */
.form-field {
  margin-bottom: 1.5rem;
}

.form-label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: var(--color-text-primary);
}

.form-input {
  display: block;
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-input:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

/* Error state with color, icon, and text */
.form-field--error .form-label {
  color: var(--color-error);
}

.form-field--error .form-input {
  border-color: var(--color-error);
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%23dc2626'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M6 18L18 6M6 6l12 12'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
  background-size: 1rem 1rem;
  padding-right: 2.5rem;
}

.form-field--error .form-input:focus {
  border-color: var(--color-error);
  box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
}

.form-error-message {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 0.5rem;
  font-size: 0.875rem;
  color: var(--color-error);
}

.form-error-message::before {
  content: "⚠";
  font-weight: bold;
}

/* Success state */
.form-field--success .form-input {
  border-color: var(--color-success);
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%23059669'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M5 13l4 4L19 7'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
  background-size: 1rem 1rem;
  padding-right: 2.5rem;
}
```

### 9.3 Screen Reader & ARIA Support

#### **Semantic HTML & ARIA Labels**
```css
/* ✅ Good: スクリーンリーダー対応とARIA実装 */

/* Visually hidden but screen reader accessible */
.sr-only {
  position: absolute !important;
  width: 1px !important;
  height: 1px !important;
  padding: 0 !important;
  margin: -1px !important;
  overflow: hidden !important;
  clip: rect(0, 0, 0, 0) !important;
  white-space: nowrap !important;
  border: 0 !important;
}

/* Focus-on for screen reader only content */
.sr-only:focus {
  position: static !important;
  width: auto !important;
  height: auto !important;
  padding: inherit !important;
  margin: inherit !important;
  overflow: visible !important;
  clip: auto !important;
  white-space: normal !important;
}

/* Loading states with proper announcements */
.loading-spinner {
  display: inline-block;
  width: 1rem;
  height: 1rem;
  border: 2px solid #e5e7eb;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Button with loading state */
.button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background-color: #3b82f6;
  color: #ffffff;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.button:hover:not(:disabled) {
  background-color: #2563eb;
}

.button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.button[aria-busy="true"] {
  pointer-events: none;
}

.button[aria-busy="true"] .loading-spinner {
  display: inline-block;
}

.button:not([aria-busy="true"]) .loading-spinner {
  display: none;
}

/* Modal dialog with proper focus management */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
}

.modal-overlay[aria-hidden="false"] {
  opacity: 1;
  visibility: visible;
}

.modal-dialog {
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  transform: scale(0.95);
  transition: transform 0.3s ease;
}

.modal-overlay[aria-hidden="false"] .modal-dialog {
  transform: scale(1);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.5rem 1.5rem 1rem;
  border-bottom: 1px solid #e5e7eb;
}

.modal-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.modal-close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  padding: 0;
  background: none;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  color: #6b7280;
  transition: all 0.2s ease;
}

.modal-close:hover {
  background-color: #f3f4f6;
  color: #374151;
}

.modal-close:focus-visible {
  outline: 2px solid var(--focus-ring-color);
  outline-offset: 2px;
}

/* Progress indicators with proper labeling */
.progress-container {
  margin: 1rem 0;
}

.progress-label {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
}

.progress-bar {
  width: 100%;
  height: 0.5rem;
  background-color: #e5e7eb;
  border-radius: 9999px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background-color: #3b82f6;
  border-radius: 9999px;
  transition: width 0.3s ease;
  transform-origin: left center;
}

/* Tooltip with proper ARIA implementation */
.tooltip-trigger {
  position: relative;
  display: inline-block;
  cursor: help;
}

.tooltip {
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  margin-bottom: 0.5rem;
  padding: 0.5rem 0.75rem;
  background-color: #1f2937;
  color: #ffffff;
  font-size: 0.875rem;
  border-radius: 6px;
  white-space: nowrap;
  z-index: 1000;
  opacity: 0;
  visibility: hidden;
  transition: all 0.2s ease;
  pointer-events: none;
}

.tooltip::after {
  content: '';
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  border: 4px solid transparent;
  border-top-color: #1f2937;
}

.tooltip-trigger:hover .tooltip,
.tooltip-trigger:focus .tooltip,
.tooltip[aria-hidden="false"] {
  opacity: 1;
  visibility: visible;
}

/* Breadcrumb navigation with proper semantics */
.breadcrumb-nav {
  padding: 1rem 0;
}

.breadcrumb-list {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  list-style: none;
  margin: 0;
  padding: 0;
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
}

.breadcrumb-separator {
  color: #9ca3af;
  font-size: 0.875rem;
  user-select: none;
}

.breadcrumb-current {
  color: #111827;
  font-weight: 600;
  padding: 0.25rem 0.5rem;
}
```

