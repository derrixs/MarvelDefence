param(
    [string]$WaterEdit,
    [string]$BoatSheet
)

# Preserva os pixels originais fora dos dois trechos de água. Não redesenha o cenário.
Add-Type -AssemblyName System.Drawing
$assetRoot = Join-Path $PSScriptRoot '../../app/src/main/res/drawable-nodpi'
$original = [System.Drawing.Bitmap]::new((Join-Path $assetRoot 'campaign_tower.png'))
$water = [System.Drawing.Bitmap]::new($WaterEdit)
$background = [System.Drawing.Bitmap]::new($original)
$graphics = [System.Drawing.Graphics]::FromImage($background)
$patches = @(
    @(120, 800, 150, 740, 175, 700, 230, 700, 265, 820, 255, 875, 125, 875),
    @(315, 865, 350, 765, 395, 765, 440, 840, 470, 875, 465, 935, 310, 938)
)

foreach ($xy in $patches) {
    $points = for ($index = 0; $index -lt $xy.Count; $index += 2) {
        [System.Drawing.Point]::new($xy[$index], $xy[$index + 1])
    }
    $mask = [System.Drawing.Drawing2D.GraphicsPath]::new()
    $mask.AddPolygon([System.Drawing.Point[]]$points)
    $bounds = $mask.GetBounds()
    for ($y = [int]$bounds.Top; $y -le $bounds.Bottom; $y++) {
        for ($x = [int]$bounds.Left; $x -le $bounds.Right; $x++) {
            if (!$mask.IsVisible($x, $y)) {
                continue
            }
            $distance = 1000.0
            for ($edge = 0; $edge -lt $points.Count; $edge++) {
                $a = $points[$edge]
                $b = $points[($edge + 1) % $points.Count]
                $dx = $b.X - $a.X
                $dy = $b.Y - $a.Y
                $t = [Math]::Clamp((($x - $a.X) * $dx + ($y - $a.Y) * $dy) / ($dx * $dx + $dy * $dy), 0.0, 1.0)
                $distance = [Math]::Min($distance, [Math]::Sqrt([Math]::Pow($x - $a.X - $t * $dx, 2) + [Math]::Pow($y - $a.Y - $t * $dy, 2)))
            }
            $blend = [Math]::Min(1.0, $distance / 8.0)
            $source = $original.GetPixel($x, $y)
            $replacement = $water.GetPixel($x, $y)
            $color = [System.Drawing.Color]::FromArgb(
                255,
                [int]($source.R * (1 - $blend) + $replacement.R * $blend),
                [int]($source.G * (1 - $blend) + $replacement.G * $blend),
                [int]($source.B * (1 - $blend) + $replacement.B * $blend)
            )
            $background.SetPixel($x, $y, $color)
        }
    }
    $mask.Dispose()
}

$graphics.Dispose()
$background.Save((Join-Path $assetRoot 'campaign_tower_environment.png'), [System.Drawing.Imaging.ImageFormat]::Png)

# Recorta a transparência em cada metade da imagem gerada, preservando o canal alfa.
$sheet = [System.Drawing.Bitmap]::new($BoatSheet)
for ($boat = 0; $boat -lt 2; $boat++) {
    $left = [int]($sheet.Width * $boat / 2)
    $right = [int]($sheet.Width * ($boat + 1) / 2)
    $minX = $right
    $maxX = $left
    $minY = $sheet.Height
    $maxY = 0
    for ($y = 0; $y -lt $sheet.Height; $y++) {
        for ($x = $left; $x -lt $right; $x++) {
            if ($sheet.GetPixel($x, $y).A -gt 8) {
                $minX = [Math]::Min($minX, $x)
                $maxX = [Math]::Max($maxX, $x)
                $minY = [Math]::Min($minY, $y)
                $maxY = [Math]::Max($maxY, $y)
            }
        }
    }
    $rectangle = [System.Drawing.Rectangle]::new($minX, $minY, $maxX - $minX + 1, $maxY - $minY + 1)
    $sprite = $sheet.Clone($rectangle, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $name = if ($boat -eq 0) { 'environment_boat_one.png' } else { 'environment_boat_two.png' }
    $sprite.Save((Join-Path $assetRoot $name), [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Output "$name : $($sprite.Width)x$($sprite.Height), transparent alpha retained"
    $sprite.Dispose()
}

$sheet.Dispose()
$background.Dispose()
$original.Dispose()
$water.Dispose()
