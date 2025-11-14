#!/bin/bash

################################################################################
# 参照整合性チェックスクリプト
# 
# 目的: 00-guidesドキュメントで参照されているパスが実際に存在するか検証
#
# 使用方法: bash check-reference-consistency.sh
#
# 出力: 存在しない参照パスのリスト
################################################################################

BASE_DIR="/mnt/aidrive/devin-organization-standards"
GUIDES_DIR="$BASE_DIR/00-guides"

echo "=================================="
echo "参照整合性チェック"
echo "=================================="
echo ""
echo "対象: 00-guides内のドキュメント"
echo ""

total_refs=0
valid_refs=0
invalid_refs=0

# 主要ガイドをチェック
MAIN_GUIDES=(
    "AI-MASTER-WORKFLOW-GUIDE.md"
    "DOCUMENT-USAGE-MANUAL.md"
    "AI-DELIVERABLE-REFERENCE-GUIDE.md"
)

for guide in "${MAIN_GUIDES[@]}"; do
    guide_path="$GUIDES_DIR/$guide"
    
    if [ ! -f "$guide_path" ]; then
        echo "⚠️  $guide が見つかりません"
        continue
    fi
    
    echo "📄 チェック中: $guide"
    echo ""
    
    # パターン1: `フォルダ/サブフォルダ/ファイル.md` 形式の参照を抽出
    # パターン2: フォルダ/サブフォルダ/ 形式の参照を抽出
    refs=$(grep -oE '([0-9][0-9][-a-z]+/[-a-z/]+\.(md|MD)|[0-9][0-9][-a-z]+/[-a-z]+/)' "$guide_path" | sort -u)
    
    while IFS= read -r ref; do
        if [ -z "$ref" ]; then
            continue
        fi
        
        ((total_refs++))
        
        # フルパスを構築
        full_path="$BASE_DIR/$ref"
        
        # ディレクトリ参照の場合（末尾が/）
        if [[ "$ref" == */ ]]; then
            if [ -d "$full_path" ]; then
                ((valid_refs++))
                echo "  ✅ $ref"
            else
                ((invalid_refs++))
                echo "  ❌ $ref (ディレクトリ不在)"
            fi
        else
            # ファイル参照の場合
            if [ -f "$full_path" ]; then
                ((valid_refs++))
                echo "  ✅ $ref"
            else
                ((invalid_refs++))
                echo "  ❌ $ref (ファイル不在)"
            fi
        fi
    done <<< "$refs"
    
    echo ""
done

echo "=================================="
echo "チェック結果サマリー"
echo "=================================="
echo ""
echo "総参照数: $total_refs"
echo "✅ 有効: $valid_refs"
echo "❌ 無効: $invalid_refs"
echo ""

if [ $invalid_refs -gt 0 ]; then
    echo "⚠️  無効な参照が見つかりました"
    echo "推奨アクション: パスを修正するか、参照先を作成してください"
    exit 1
else
    echo "✅ すべての参照は有効です"
    exit 0
fi
