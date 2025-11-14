#!/bin/bash

################################################################################
# メタデータ整合性チェックスクリプト
# 
# 目的: 00-guidesドキュメントのYAMLフロントマター（last_updated）と
#       実ファイル更新日時の乖離を検出
#
# 使用方法: bash check-metadata-consistency.sh
#
# 出力: 不整合があるファイルのリストと乖離日数
################################################################################

set -e

# カラー定義
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 設定
GUIDES_DIR="/mnt/aidrive/devin-organization-standards/00-guides"
THRESHOLD_DAYS=2  # 許容する乖離日数（2日以上の乖離で警告）

echo "=================================="
echo "メタデータ整合性チェック"
echo "=================================="
echo ""
echo "対象ディレクトリ: $GUIDES_DIR"
echo "許容乖離: ${THRESHOLD_DAYS}日"
echo ""

# 結果カウンター
total_files=0
inconsistent_files=0
consistent_files=0

# 一時ファイル
TEMP_REPORT=$(mktemp)

echo "チェック中..."
echo ""

# 00-guides内の全.mdファイルをチェック
while IFS= read -r file; do
    ((total_files++))
    
    filename=$(basename "$file")
    
    # YAMLフロントマターからlast_updatedを抽出
    last_updated=$(grep -m 1 "^last_updated:" "$file" 2>/dev/null | sed 's/last_updated: *"\?\([0-9-]*\)"\?/\1/' | tr -d ' ')
    
    # YAMLフロントマターがない場合はスキップ
    if [ -z "$last_updated" ]; then
        echo -e "${YELLOW}⚠ $filename: メタデータなし（YAMLフロントマター不在）${NC}"
        continue
    fi
    
    # ファイルの最終更新日を取得（YYYY-MM-DD形式）
    file_modified=$(stat -c %y "$file" | cut -d' ' -f1)
    
    # 日付を秒に変換して差を計算
    last_updated_sec=$(date -d "$last_updated" +%s 2>/dev/null || echo "0")
    file_modified_sec=$(date -d "$file_modified" +%s)
    
    # last_updatedのパースに失敗した場合
    if [ "$last_updated_sec" = "0" ]; then
        echo -e "${YELLOW}⚠ $filename: 不正なlast_updated形式 ($last_updated)${NC}"
        continue
    fi
    
    # 差分を日数に変換
    diff_sec=$((file_modified_sec - last_updated_sec))
    diff_days=$((diff_sec / 86400))
    
    # 負の値の場合は絶対値に
    if [ $diff_days -lt 0 ]; then
        diff_days=$((diff_days * -1))
    fi
    
    # 判定
    if [ $diff_days -ge $THRESHOLD_DAYS ]; then
        ((inconsistent_files++))
        echo -e "${RED}❌ $filename${NC}"
        echo "   メタデータ last_updated: $last_updated"
        echo "   実ファイル更新日: $file_modified"
        echo "   乖離: ${diff_days}日"
        echo ""
        
        # レポートに記録
        echo "$filename|$last_updated|$file_modified|$diff_days" >> "$TEMP_REPORT"
    else
        ((consistent_files++))
        echo -e "${GREEN}✅ $filename (乖離: ${diff_days}日)${NC}"
    fi
    
done < <(find "$GUIDES_DIR" -maxdepth 1 -name "*.md" -type f)

echo ""
echo "=================================="
echo "チェック結果サマリー"
echo "=================================="
echo ""
echo "総ファイル数: $total_files"
echo -e "${GREEN}整合: $consistent_files${NC}"
echo -e "${RED}不整合: $inconsistent_files${NC}"
echo ""

# 不整合がある場合、詳細レポートを生成
if [ $inconsistent_files -gt 0 ]; then
    echo "=================================="
    echo "不整合ファイル詳細"
    echo "=================================="
    echo ""
    echo "| ファイル名 | メタデータ | 実更新日 | 乖離(日) |"
    echo "|-----------|----------|---------|---------|"
    
    while IFS='|' read -r filename last_updated file_modified diff_days; do
        echo "| $filename | $last_updated | $file_modified | $diff_days |"
    done < "$TEMP_REPORT"
    
    echo ""
    echo -e "${YELLOW}推奨アクション: 上記ファイルのYAMLフロントマター（last_updated）を更新してください${NC}"
    
    # レポートファイルとして保存
    REPORT_FILE="/home/user/metadata-consistency-report-$(date +%Y%m%d-%H%M%S).txt"
    {
        echo "メタデータ整合性チェックレポート"
        echo "実行日時: $(date '+%Y-%m-%d %H:%M:%S')"
        echo ""
        echo "不整合ファイル:"
        echo ""
        while IFS='|' read -r filename last_updated file_modified diff_days; do
            echo "- $filename"
            echo "  メタデータ: $last_updated"
            echo "  実更新日: $file_modified"
            echo "  乖離: ${diff_days}日"
            echo ""
        done < "$TEMP_REPORT"
    } > "$REPORT_FILE"
    
    echo ""
    echo "詳細レポート: $REPORT_FILE"
    
    # 終了コード1で終了（CI/CDで検出可能）
    rm -f "$TEMP_REPORT"
    exit 1
else
    echo -e "${GREEN}✅ すべてのファイルのメタデータは整合しています${NC}"
    rm -f "$TEMP_REPORT"
    exit 0
fi
