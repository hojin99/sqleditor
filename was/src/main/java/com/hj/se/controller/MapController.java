package com.hj.se.controller;

import com.hj.se.mapbase.utils.TileSystemModule;
import org.springframework.web.bind.annotation.*;

/**
 * @author hojin cho
 */
@RestController
@RequestMapping("api/map")
public class MapController {

    public MapController() {

    }

    @GetMapping("/tile/{z}/{x}/{y}")
    public String getTile(@PathVariable String z, @PathVariable String x, @PathVariable String y) throws Exception {

        TileSystemModule module = new TileSystemModule(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z));





        return String.format("%s %s %s", z, x, y);
    }

    

}
