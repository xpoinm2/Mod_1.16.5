package com.example.examplemod.util;

import com.example.examplemod.ExampleMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Утилита для организации и анализа текстур мода.
 * 
 * ЦЕЛЬ: Помочь при масштабировании до 500+ текстур через:
 * 1. Анализ существующих текстур
 * 2. Группировку по паттернам
 * 3. Рекомендации по оптимизации (base + overlay)
 * 
 * ИСПОЛЬЗОВАНИЕ:
 * - Запусти TextureOrganizer.analyzeTextures() при старте сервера
 * - Проверь логи для рекомендаций
 */
public class TextureOrganizer {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Паттерны для группировки текстур
    private static final String[] ORE_PATTERNS = {"_ore", "ore_", "roasted_", "hot_", "sponge_"};
    private static final String[] TOOL_PATTERNS = {"_pickaxe", "_axe", "_shovel", "_hoe", "_sword", "_hammer", "_knife"};
    private static final String[] FOOD_PATTERNS = {"berry", "root", "ginger", "horseradish"};
    private static final String[] MATERIAL_PATTERNS = {"_ingot", "_plate", "_dust", "_wire", "_gear", "gravel"};
    
    /**
     * Группа схожих текстур с рекомендациями по оптимизации.
     */
    public static class TextureGroup {
        public final String name;
        public final List<String> textures;
        public final String recommendation;
        
        public TextureGroup(String name, List<String> textures, String recommendation) {
            this.name = name;
            this.textures = textures;
            this.recommendation = recommendation;
        }
        
        @Override
        public String toString() {
            return String.format("%s (%d textures): %s", name, textures.size(), recommendation);
        }
    }
    
    /**
     * Проанализировать существующие текстуры и выдать рекомендации.
     * Вызывай из ExampleMod.commonSetup() для анализа при старте.
     */
    public static void analyzeTextures() {
        try {
            Path texturesPath = getTexturesPath();
            if (!Files.exists(texturesPath)) {
                LOGGER.warn("Textures directory not found: {}", texturesPath);
                return;
            }
            
            List<String> allTextures = listAllTextures(texturesPath);
            LOGGER.info("======= TEXTURE ANALYZER =======");
            LOGGER.info("Found {} textures in total", allTextures.size());
            
            Map<String, TextureGroup> groups = groupTextures(allTextures);
            
            LOGGER.info("Texture groups analysis:");
            groups.forEach((key, group) -> {
                LOGGER.info("  [{}]", group);
                if (group.textures.size() > 1) {
                    LOGGER.info("    Files: {}", String.join(", ", group.textures));
                }
            });
            
            provideOptimizationRecommendations(groups, allTextures.size());
            LOGGER.info("================================");
            
        } catch (Exception e) {
            LOGGER.error("Failed to analyze textures", e);
        }
    }
    
    /**
     * Получить путь к папке с текстурами.
     */
    private static Path getTexturesPath() {
        // Попытка найти путь к текстурам (работает в dev среде)
        Path devPath = Paths.get("src/main/resources/assets/" + ExampleMod.MODID + "/textures/item");
        if (Files.exists(devPath)) {
            return devPath;
        }
        
        // Если не найдено, возвращаем относительный путь
        return Paths.get("textures/item");
    }
    
    /**
     * Список всех PNG текстур в папке.
     */
    private static List<String> listAllTextures(Path texturesPath) throws IOException {
        return Files.walk(texturesPath)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".png"))
                .filter(p -> !p.toString().endsWith(".mcmeta"))
                .map(p -> p.getFileName().toString().replace(".png", ""))
                .collect(Collectors.toList());
    }
    
    /**
     * Сгруппировать текстуры по паттернам.
     */
    private static Map<String, TextureGroup> groupTextures(List<String> textures) {
        Map<String, TextureGroup> groups = new LinkedHashMap<>();
        
        // Группируем руды
        List<String> ores = textures.stream()
                .filter(t -> matchesAny(t, ORE_PATTERNS))
                .collect(Collectors.toList());
        if (!ores.isEmpty()) {
            groups.put("ores", new TextureGroup(
                    "Ores & Metals",
                    ores,
                    "Consider base texture + colored overlay for variants"
            ));
        }
        
        // Группируем инструменты
        List<String> tools = textures.stream()
                .filter(t -> matchesAny(t, TOOL_PATTERNS))
                .collect(Collectors.toList());
        if (!tools.isEmpty()) {
            groups.put("tools", new TextureGroup(
                    "Tools & Weapons",
                    tools,
                    "Use base tool shape + material overlay (stone/bone/metal)"
            ));
        }
        
        // Группируем еду
        List<String> food = textures.stream()
                .filter(t -> matchesAny(t, FOOD_PATTERNS))
                .collect(Collectors.toList());
        if (!food.isEmpty()) {
            groups.put("food", new TextureGroup(
                    "Food Items",
                    food,
                    "Similar food items can share base template"
            ));
        }
        
        // Группируем материалы
        List<String> materials = textures.stream()
                .filter(t -> matchesAny(t, MATERIAL_PATTERNS))
                .collect(Collectors.toList());
        if (!materials.isEmpty()) {
            groups.put("materials", new TextureGroup(
                    "Materials & Components",
                    materials,
                    "Perfect candidates for base + overlay system"
            ));
        }
        
        // Остальные (уникальные)
        Set<String> grouped = new HashSet<>();
        groups.values().forEach(g -> grouped.addAll(g.textures));
        
        List<String> unique = textures.stream()
                .filter(t -> !grouped.contains(t))
                .collect(Collectors.toList());
        if (!unique.isEmpty()) {
            groups.put("unique", new TextureGroup(
                    "Unique Items",
                    unique,
                    "Keep as individual textures"
            ));
        }
        
        return groups;
    }
    
    /**
     * Выдать рекомендации по оптимизации на основе анализа.
     */
    private static void provideOptimizationRecommendations(Map<String, TextureGroup> groups, int totalTextures) {
        LOGGER.info("=== OPTIMIZATION RECOMMENDATIONS ===");
        
        int groupedTextures = groups.values().stream()
                .filter(g -> !g.name.equals("Unique Items"))
                .mapToInt(g -> g.textures.size())
                .sum();
        
        double groupedPercent = (groupedTextures * 100.0) / totalTextures;
        
        LOGGER.info("Textures that can be optimized: {} / {} ({:.1f}%)", 
                groupedTextures, totalTextures, groupedPercent);
        
        if (totalTextures < 100) {
            LOGGER.info("✓ Current texture count ({}) is manageable", totalTextures);
            LOGGER.info("  Continue with individual textures for now");
        } else if (totalTextures < 300) {
            LOGGER.info("⚠ Approaching optimization threshold ({} textures)", totalTextures);
            LOGGER.info("  Consider base+overlay for new groups (ores, tools)");
        } else {
            LOGGER.warn("❗ HIGH texture count ({} textures)", totalTextures);
            LOGGER.warn("  Strongly recommend implementing base+overlay system");
            LOGGER.warn("  Expected memory savings: 40-60% with proper atlasing");
        }
        
        // Специфичные рекомендации для больших групп
        groups.forEach((key, group) -> {
            if (group.textures.size() >= 5 && !group.name.equals("Unique Items")) {
                LOGGER.info("→ Group '{}' has {} textures - GOOD candidate for base+overlay",
                        group.name, group.textures.size());
            }
        });
    }
    
    /**
     * Проверка совпадения с любым из паттернов.
     */
    private static boolean matchesAny(String texture, String[] patterns) {
        for (String pattern : patterns) {
            if (texture.contains(pattern)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * ОПЦИОНАЛЬНО: Генерация инструкции по реорганизации текстур.
     * Создаёт Markdown файл с рекомендациями.
     */
    public static void generateReorganizationGuide() {
        LOGGER.info("Generating texture reorganization guide...");
        
        StringBuilder guide = new StringBuilder();
        guide.append("# Texture Reorganization Guide\n\n");
        guide.append("## Current Structure\n");
        guide.append("```\n");
        guide.append("textures/item/\n");
        guide.append("  - all textures in one folder\n");
        guide.append("```\n\n");
        
        guide.append("## Recommended Structure (for 500+ textures)\n");
        guide.append("```\n");
        guide.append("textures/item/\n");
        guide.append("  base/\n");
        guide.append("    - ore_base.png          # Base shape for all ores\n");
        guide.append("    - tool_pickaxe_base.png # Base shape for pickaxes\n");
        guide.append("    - ingot_base.png        # Base shape for ingots\n");
        guide.append("  overlay/\n");
        guide.append("    - metal_iron.png        # Iron color overlay\n");
        guide.append("    - metal_copper.png      # Copper color overlay\n");
        guide.append("    - metal_tin.png         # Tin color overlay\n");
        guide.append("  generated/\n");
        guide.append("    - iron_ore.png          # Generated: base + overlay\n");
        guide.append("    - copper_ore.png        # Generated: base + overlay\n");
        guide.append("```\n\n");
        
        guide.append("## Implementation Steps\n");
        guide.append("1. Identify similar texture groups (ores, tools, ingots)\n");
        guide.append("2. Create base templates (shapes without colors)\n");
        guide.append("3. Create color overlays (colors without shapes)\n");
        guide.append("4. Use image processing to combine base + overlay\n");
        guide.append("5. Save combined textures to generated/ folder\n\n");
        
        guide.append("## Benefits\n");
        guide.append("- Add new metal: just create 1 overlay instead of 10+ textures\n");
        guide.append("- Memory: 500 textures → ~100 base + 50 overlays = 150 files (70% reduction)\n");
        guide.append("- Consistency: all ores/tools look uniform\n");
        
        LOGGER.info("Guide generated. Save to TEXTURE_GUIDE.md manually if needed");
        LOGGER.info(guide.toString());
    }
}

