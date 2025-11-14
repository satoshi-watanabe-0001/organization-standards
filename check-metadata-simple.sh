#!/bin/bash

# シンプルなメタデータ整合性チェックスクリプト

GUIDES_DIR="/mnt/aidrive/devin-organization-standards/00-guides"

echo "メタデータ整合性チェック"
echo "======================="
echo ""

for file in "$GUIDES_DIR"/*.md; do
    filename=$(basename "$file")
    
    # last_updatedを抽出
    last_updated=$(grep "^last_updated:" "$file" 2>/dev/null | head -1 | sed 's/.*: *"\?\([0-9-]*\)"\?.*/\1/')
    
    # ファイル更新日を取得
    file_date=$(stat -c %y "$file" | cut -d' ' -f1)
    
    if [ -n "$last_updated" ]; then
        echo "📄 $filename"
        echo "   メタデータ: $last_updated"
        echo "   実更新日: $file_date"
        
        # 日付比較
        if [ "$last_updated" = "$file_date" ]; then
            echo "   ✅ 一致"
        else
            echo "   ⚠️ 不一致"
        fi
        echo ""
    fi
done

echo "チェック完了"
